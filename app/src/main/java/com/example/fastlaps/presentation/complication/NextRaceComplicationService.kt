package com.example.fastlaps.presentation.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import com.leandro.fastlaps.R
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.example.fastlaps.presentation.MainActivity
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import com.example.fastlaps.presentation.util.F1Constants
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

class NextRaceComplicationService : SuspendingComplicationDataSourceService() {

    private val repository = DriverStandingsRepository()

    private fun complicationIcon() = MonochromaticImage.Builder(
        Icon.createWithResource(this, R.drawable.ic_complication_flag)
    ).build()

    override suspend fun onComplicationRequest(
        request: ComplicationRequest
    ): ComplicationData? {
        val tapAction = createTapAction()

        return try {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val races = repository.getRaceSchedule(year)
            val today = LocalDate.now()
            val nextRace = races.firstOrNull {
                try { LocalDate.parse(it.date) >= today } catch (_: Exception) { false }
            }

            if (nextRace != null) {
                val raceDate = LocalDate.parse(nextRace.date)
                val daysUntil = ChronoUnit.DAYS.between(today, raceDate)
                val flag = F1Constants.countryFlag(nextRace.Circuit.Location.country)
                val name = nextRace.raceName.replace(" Grand Prix", "")
                val shortName = nextRace.Circuit.Location.country.take(3).uppercase()

                val countdownShort = when (daysUntil) {
                    0L -> "HOY"
                    1L -> "1d"
                    else -> "${daysUntil}d"
                }
                val countdownLong = when (daysUntil) {
                    0L -> "Today!"
                    1L -> "Tomorrow"
                    else -> "in $daysUntil days"
                }

                when (request.complicationType) {
                    ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                        text = PlainComplicationText.Builder(countdownShort).build(),
                        contentDescription = PlainComplicationText.Builder("$name $countdownLong").build()
                    )
                        .setTitle(PlainComplicationText.Builder(shortName).build())
                        .setMonochromaticImage(complicationIcon())
                        .setTapAction(tapAction)
                        .build()

                    ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                        text = PlainComplicationText.Builder("$flag $name - $countdownLong").build(),
                        contentDescription = PlainComplicationText.Builder("Next race: $name $countdownLong").build()
                    )
                        .setTitle(PlainComplicationText.Builder("Next Race").build())
                        .setMonochromaticImage(complicationIcon())
                        .setTapAction(tapAction)
                        .build()

                    ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                        monochromaticImage = complicationIcon(),
                        contentDescription = PlainComplicationText.Builder("$name $countdownLong").build()
                    )
                        .setTapAction(tapAction)
                        .build()

                    else -> null
                }
            } else {
                buildNoRaceData(request.complicationType, tapAction)
            }
        } catch (_: Exception) {
            buildNoRaceData(request.complicationType, tapAction)
        }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("3d").build(),
                contentDescription = PlainComplicationText.Builder("Australia in 3 days").build()
            )
                .setTitle(PlainComplicationText.Builder("AUS").build())
                .setMonochromaticImage(complicationIcon())
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Australia - in 3 days").build(),
                contentDescription = PlainComplicationText.Builder("Next race").build()
            )
                .setTitle(PlainComplicationText.Builder("Next Race").build())
                .setMonochromaticImage(complicationIcon())
                .build()

            ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                monochromaticImage = complicationIcon(),
                contentDescription = PlainComplicationText.Builder("Next race: Australia in 3 days").build()
            ).build()

            else -> null
        }
    }

    private fun buildNoRaceData(
        type: ComplicationType,
        tapAction: PendingIntent
    ): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("—").build(),
                contentDescription = PlainComplicationText.Builder("No upcoming races").build()
            )
                .setTitle(PlainComplicationText.Builder("F1").build())
                .setTapAction(tapAction)
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("No upcoming races").build(),
                contentDescription = PlainComplicationText.Builder("No upcoming races").build()
            )
                .setTitle(PlainComplicationText.Builder("Next Race").build())
                .setTapAction(tapAction)
                .build()

            ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                monochromaticImage = complicationIcon(),
                contentDescription = PlainComplicationText.Builder("No upcoming races").build()
            )
                .setTapAction(tapAction)
                .build()

            else -> null
        }
    }

    private fun createTapAction(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
