package com.example.fastlaps.presentation.tile

import Race
import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.protolayout.TimelineBuilders
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import com.example.fastlaps.presentation.util.F1Constants
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

class NextRaceTileService : TileService() {

    private val repository = DriverStandingsRepository()
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<TileBuilders.Tile> {
        return CallbackToFutureAdapter.getFuture { completer ->
            serviceScope.launch {
                try {
                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    val races = repository.getRaceSchedule(year)
                    val today = LocalDate.now()
                    val nextRace = races.firstOrNull {
                        try {
                            LocalDate.parse(it.date) >= today
                        } catch (_: Exception) {
                            false
                        }
                    }
                    completer.set(
                        buildTile(requestParams.deviceConfiguration, nextRace, today)
                    )
                } catch (_: Exception) {
                    completer.set(
                        buildFallbackTile(requestParams.deviceConfiguration)
                    )
                }
            }
            "NextRaceTile"
        }
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ListenableFuture<ResourceBuilders.Resources> {
        return CallbackToFutureAdapter.getFuture { completer ->
            completer.set(
                ResourceBuilders.Resources.Builder()
                    .setVersion("1")
                    .build()
            )
            "NextRaceTileResources"
        }
    }

    private fun buildTile(
        deviceParams: DeviceParametersBuilders.DeviceParameters,
        nextRace: Race?,
        today: LocalDate
    ): TileBuilders.Tile {
        val layout = if (nextRace != null) {
            buildNextRaceLayout(deviceParams, nextRace, today)
        } else {
            buildNoRaceLayout(deviceParams)
        }

        return wrapInTile(layout)
    }

    private fun buildNextRaceLayout(
        deviceParams: DeviceParametersBuilders.DeviceParameters,
        race: Race,
        today: LocalDate
    ): LayoutElementBuilders.LayoutElement {
        val raceDate = LocalDate.parse(race.date)
        val daysUntil = ChronoUnit.DAYS.between(today, raceDate)
        val flag = F1Constants.countryFlag(race.Circuit.Location.country)
        val name = race.raceName.replace(" Grand Prix", "")
        val lang = getLanguage()

        val topLabel = if (lang == "es") "Próxima Carrera" else "Next Race"
        val countdownText = when {
            daysUntil == 0L -> if (lang == "es") "¡Hoy!" else "Today!"
            daysUntil == 1L -> if (lang == "es") "Mañana" else "Tomorrow"
            else -> if (lang == "es") "en $daysUntil días" else "in $daysUntil days"
        }

        val clickable = ModifiersBuilders.Clickable.Builder()
            .setId("open_app")
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName("com.leandro.fastlaps")
                            .setClassName("com.example.fastlaps.presentation.MainActivity")
                            .build()
                    )
                    .build()
            )
            .build()

        return PrimaryLayout.Builder(deviceParams)
            .setPrimaryLabelTextContent(
                Text.Builder(this, topLabel)
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(0xFFAAAAAA.toInt()))
                    .build()
            )
            .setContent(
                Text.Builder(this, "$flag $name")
                    .setTypography(Typography.TYPOGRAPHY_TITLE3)
                    .setColor(argb(0xFFFFFFFF.toInt()))
                    .setModifiers(
                        ModifiersBuilders.Modifiers.Builder()
                            .setClickable(clickable)
                            .build()
                    )
                    .build()
            )
            .setSecondaryLabelTextContent(
                Text.Builder(this, countdownText)
                    .setTypography(Typography.TYPOGRAPHY_BODY1)
                    .setColor(argb(0xFFFF9800.toInt()))
                    .build()
            )
            .build()
    }

    private fun buildNoRaceLayout(
        deviceParams: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        val lang = getLanguage()
        val message = if (lang == "es") "Sin carreras próximas" else "No upcoming races"

        return PrimaryLayout.Builder(deviceParams)
            .setContent(
                Text.Builder(this, message)
                    .setTypography(Typography.TYPOGRAPHY_BODY1)
                    .setColor(argb(0xFFAAAAAA.toInt()))
                    .build()
            )
            .build()
    }

    private fun buildFallbackTile(
        deviceParams: DeviceParametersBuilders.DeviceParameters
    ): TileBuilders.Tile {
        val lang = getLanguage()
        val message = if (lang == "es") "Toca para abrir" else "Tap to open"

        val clickable = ModifiersBuilders.Clickable.Builder()
            .setId("open_app")
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName("com.leandro.fastlaps")
                            .setClassName("com.example.fastlaps.presentation.MainActivity")
                            .build()
                    )
                    .build()
            )
            .build()

        val layout = PrimaryLayout.Builder(deviceParams)
            .setPrimaryLabelTextContent(
                Text.Builder(this, "FastLaps")
                    .setTypography(Typography.TYPOGRAPHY_TITLE3)
                    .setColor(argb(0xFFFF9800.toInt()))
                    .build()
            )
            .setContent(
                Text.Builder(this, message)
                    .setTypography(Typography.TYPOGRAPHY_BODY1)
                    .setColor(argb(0xFFAAAAAA.toInt()))
                    .setModifiers(
                        ModifiersBuilders.Modifiers.Builder()
                            .setClickable(clickable)
                            .build()
                    )
                    .build()
            )
            .build()

        return wrapInTile(layout)
    }

    private fun wrapInTile(layout: LayoutElementBuilders.LayoutElement): TileBuilders.Tile {
        return TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(
                                LayoutElementBuilders.Layout.Builder()
                                    .setRoot(layout)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .setFreshnessIntervalMillis(60 * 60 * 1000)
            .build()
    }

    private fun getLanguage(): String {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }
}
