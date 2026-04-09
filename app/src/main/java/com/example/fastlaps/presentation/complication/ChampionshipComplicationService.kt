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
import com.leandro.fastlaps.R
import com.example.fastlaps.presentation.MainActivity
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import java.util.Calendar

class ChampionshipComplicationService : SuspendingComplicationDataSourceService() {

    private val repository = DriverStandingsRepository()

    private fun complicationIcon() = MonochromaticImage.Builder(
        Icon.createWithResource(this, R.drawable.ic_complication_trophy)
    ).build()

    override suspend fun onComplicationRequest(
        request: ComplicationRequest
    ): ComplicationData? {
        val tapAction = createTapAction()

        return try {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val standings = repository.getProcessedDriverStandings(year)
            val leader = standings.firstOrNull()

            if (leader != null) {
                val code = leader.Driver.code
                    ?: leader.Driver.familyName.take(3).uppercase()
                val points = leader.points
                val fullName = "${leader.Driver.givenName} ${leader.Driver.familyName}"

                when (request.complicationType) {
                    ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                        text = PlainComplicationText.Builder("${points}pts").build(),
                        contentDescription = PlainComplicationText.Builder("$fullName P1 $points points").build()
                    )
                        .setTitle(PlainComplicationText.Builder("$code P1").build())
                        .setMonochromaticImage(complicationIcon())
                        .setTapAction(tapAction)
                        .build()

                    ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                        text = PlainComplicationText.Builder("$fullName P1 - ${points}pts").build(),
                        contentDescription = PlainComplicationText.Builder("Championship leader").build()
                    )
                        .setTitle(PlainComplicationText.Builder("Championship").build())
                        .setMonochromaticImage(complicationIcon())
                        .setTapAction(tapAction)
                        .build()

                    ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                        monochromaticImage = complicationIcon(),
                        contentDescription = PlainComplicationText.Builder("$fullName P1 $points points").build()
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
                text = PlainComplicationText.Builder("283pts").build(),
                contentDescription = PlainComplicationText.Builder("Verstappen P1").build()
            )
                .setTitle(PlainComplicationText.Builder("VER P1").build())
                .setMonochromaticImage(complicationIcon())
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Verstappen P1 - 283pts").build(),
                contentDescription = PlainComplicationText.Builder("Championship leader").build()
            )
                .setTitle(PlainComplicationText.Builder("Championship").build())
                .setMonochromaticImage(complicationIcon())
                .build()

            ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                monochromaticImage = complicationIcon(),
                contentDescription = PlainComplicationText.Builder("Championship leader").build()
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
                contentDescription = PlainComplicationText.Builder("No data").build()
            )
                .setTitle(PlainComplicationText.Builder("F1").build())
                .setTapAction(tapAction)
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("No data available").build(),
                contentDescription = PlainComplicationText.Builder("No data").build()
            )
                .setTitle(PlainComplicationText.Builder("Championship").build())
                .setTapAction(tapAction)
                .build()

            ComplicationType.MONOCHROMATIC_IMAGE -> MonochromaticImageComplicationData.Builder(
                monochromaticImage = complicationIcon(),
                contentDescription = PlainComplicationText.Builder("No data").build()
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
