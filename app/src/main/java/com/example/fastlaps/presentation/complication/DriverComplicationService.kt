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
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.example.fastlaps.presentation.MainActivity
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import com.leandro.fastlaps.R
import java.util.Calendar

class DriverComplicationService : SuspendingComplicationDataSourceService() {

    private val repository = DriverStandingsRepository()

    private fun complicationIcon() = MonochromaticImage.Builder(
        Icon.createWithResource(this, R.drawable.ic_complication_driver)
    ).build()

    override suspend fun onComplicationRequest(
        request: ComplicationRequest
    ): ComplicationData? {
        val tapAction = createTapAction()
        val prefs = getSharedPreferences("driver_complication", MODE_PRIVATE)
        val driverId = prefs.getString("driver_id", null)

        if (driverId == null) {
            return buildNoDataComplication(request.complicationType, tapAction)
        }

        return try {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val standings = repository.getProcessedDriverStandings(year)
            val driver = standings.find { it.Driver.driverId == driverId }

            if (driver != null) {
                val code = driver.Driver.code.ifEmpty { driver.Driver.familyName.take(3).uppercase() }
                val name = "${driver.Driver.givenName} ${driver.Driver.familyName}"
                val points = driver.points
                val position = driver.position

                when (request.complicationType) {
                    ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                        text = PlainComplicationText.Builder("${points}pts").build(),
                        contentDescription = PlainComplicationText.Builder("$name P$position").build()
                    )
                        .setTitle(PlainComplicationText.Builder("$code P$position").build())
                        .setMonochromaticImage(complicationIcon())
                        .setTapAction(tapAction)
                        .build()

                    ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                        text = PlainComplicationText.Builder("$name P$position - ${points}pts").build(),
                        contentDescription = PlainComplicationText.Builder("$name standing").build()
                    )
                        .setTitle(PlainComplicationText.Builder(code).build())
                        .setMonochromaticImage(complicationIcon())
                        .setTapAction(tapAction)
                        .build()

                    ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                        monochromaticImage = complicationIcon(),
                        contentDescription = PlainComplicationText.Builder("$code P$position").build()
                    )
                        .setTapAction(tapAction)
                        .build()

                    else -> null
                }
            } else {
                buildNoDataComplication(request.complicationType, tapAction)
            }
        } catch (_: Exception) {
            buildNoDataComplication(request.complicationType, tapAction)
        }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("245pts").build(),
                contentDescription = PlainComplicationText.Builder("Driver standings").build()
            )
                .setTitle(PlainComplicationText.Builder("VER P1").build())
                .setMonochromaticImage(complicationIcon())
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Verstappen P1 - 245pts").build(),
                contentDescription = PlainComplicationText.Builder("Driver standing").build()
            )
                .setTitle(PlainComplicationText.Builder("VER").build())
                .setMonochromaticImage(complicationIcon())
                .build()

            ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                monochromaticImage = complicationIcon(),
                contentDescription = PlainComplicationText.Builder("Driver").build()
            ).build()

            else -> null
        }
    }

    private fun buildNoDataComplication(
        type: ComplicationType,
        tapAction: PendingIntent
    ): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("—").build(),
                contentDescription = PlainComplicationText.Builder("Select driver in app").build()
            )
                .setTitle(PlainComplicationText.Builder("F1").build())
                .setMonochromaticImage(complicationIcon())
                .setTapAction(tapAction)
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Open app to select driver").build(),
                contentDescription = PlainComplicationText.Builder("No driver").build()
            )
                .setTitle(PlainComplicationText.Builder("F1").build())
                .setMonochromaticImage(complicationIcon())
                .setTapAction(tapAction)
                .build()

            ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                monochromaticImage = complicationIcon(),
                contentDescription = PlainComplicationText.Builder("F1 Driver").build()
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
