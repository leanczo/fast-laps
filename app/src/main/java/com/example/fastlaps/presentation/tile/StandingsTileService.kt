package com.example.fastlaps.presentation.tile

import DriverStanding
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
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class StandingsTileService : TileService() {

    private val repository = DriverStandingsRepository()
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<TileBuilders.Tile> {
        return CallbackToFutureAdapter.getFuture { completer ->
            serviceScope.launch {
                try {
                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    val standings = repository.getProcessedDriverStandings(year)
                    completer.set(
                        buildTile(requestParams.deviceConfiguration, standings)
                    )
                } catch (_: Exception) {
                    completer.set(
                        buildFallbackTile(requestParams.deviceConfiguration)
                    )
                }
            }
            "StandingsTile"
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
            "StandingsTileResources"
        }
    }

    private fun buildTile(
        deviceParams: DeviceParametersBuilders.DeviceParameters,
        standings: List<DriverStanding>
    ): TileBuilders.Tile {
        val layout = if (standings.isNotEmpty()) {
            buildStandingsLayout(deviceParams, standings)
        } else {
            buildNoDataLayout(deviceParams)
        }
        return wrapInTile(layout)
    }

    private fun buildStandingsLayout(
        deviceParams: DeviceParametersBuilders.DeviceParameters,
        standings: List<DriverStanding>
    ): LayoutElementBuilders.LayoutElement {
        val lang = getLanguage()
        val title = if (lang == "es") "Pilotos" else "Drivers"

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

        val column = LayoutElementBuilders.Column.Builder()
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(clickable)
                    .build()
            )

        standings.take(5).forEach { standing ->
            val code = standing.Driver.code
                ?: standing.Driver.familyName.take(3).uppercase()
            val text = "${standing.position}. $code  ${standing.points}pts"
            val color = when (standing.position) {
                "1" -> 0xFFFFD700.toInt()
                "2" -> 0xFFC0C0C0.toInt()
                "3" -> 0xFFCD7F32.toInt()
                else -> 0xFFFFFFFF.toInt()
            }
            column.addContent(
                Text.Builder(this, text)
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(color))
                    .build()
            )
        }

        return PrimaryLayout.Builder(deviceParams)
            .setPrimaryLabelTextContent(
                Text.Builder(this, title)
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(0xFFFF9800.toInt()))
                    .build()
            )
            .setContent(column.build())
            .build()
    }

    private fun buildNoDataLayout(
        deviceParams: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        val lang = getLanguage()
        val message = if (lang == "es") "Sin datos disponibles" else "No data available"

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
            .setFreshnessIntervalMillis(30 * 60 * 1000)
            .build()
    }

    private fun getLanguage(): String {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }
}
