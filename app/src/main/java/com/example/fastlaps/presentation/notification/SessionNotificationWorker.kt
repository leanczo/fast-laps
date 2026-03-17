package com.example.fastlaps.presentation.notification

import SessionTime
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fastlaps.presentation.MainActivity
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import com.leandro.fastlaps.R
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

class SessionNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = DriverStandingsRepository()

    override suspend fun doWork(): Result {
        try {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val races = repository.getRaceSchedule(year)
            val now = ZonedDateTime.now(ZoneId.of("UTC"))
            val prefs = applicationContext.getSharedPreferences("notifications", Context.MODE_PRIVATE)
            val lang = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString("language", "en") ?: "en"

            for (race in races) {
                val sessions = listOf(
                    ("FP1" to if (lang == "es") "Práctica Libre 1" else "Free Practice 1") to race.FirstPractice,
                    ("FP2" to if (lang == "es") "Práctica Libre 2" else "Free Practice 2") to race.SecondPractice,
                    ("FP3" to if (lang == "es") "Práctica Libre 3" else "Free Practice 3") to race.ThirdPractice,
                    ("QUALI" to if (lang == "es") "Clasificación" else "Qualifying") to race.Qualifying,
                    ("SPRINT" to "Sprint") to race.Sprint,
                    ("RACE" to if (lang == "es") "Carrera" else "Race") to SessionTime(race.date, race.time)
                )

                val raceName = race.raceName.replace(" Grand Prix", "")

                for ((namesPair, sessionTime) in sessions) {
                    val (sessionKey, sessionLabel) = namesPair
                    if (sessionTime == null || sessionTime.date.isEmpty() || sessionTime.time.isEmpty()) continue

                    val sessionDateTime = try {
                        val date = LocalDate.parse(sessionTime.date)
                        val time = LocalTime.parse(sessionTime.time.removeSuffix("Z"))
                        ZonedDateTime.of(date, time, ZoneId.of("UTC"))
                    } catch (_: Exception) { continue }

                    val minutesUntil = Duration.between(now, sessionDateTime).toMinutes()
                    val notifKey = "${race.season}_${race.round}_$sessionKey"

                    // 15 minutes before
                    if (minutesUntil in 1..16) {
                        val key15 = "${notifKey}_15min"
                        if (!prefs.getBoolean(key15, false)) {
                            val text = if (lang == "es") {
                                "$sessionLabel comienza en $minutesUntil min"
                            } else {
                                "$sessionLabel starts in $minutesUntil min"
                            }
                            showNotification(
                                id = notifKey.hashCode(),
                                title = raceName,
                                text = text
                            )
                            prefs.edit().putBoolean(key15, true).apply()
                        }
                    }

                    // Starting now
                    if (minutesUntil in -5..0) {
                        val keyNow = "${notifKey}_now"
                        if (!prefs.getBoolean(keyNow, false)) {
                            val text = if (lang == "es") {
                                "$sessionLabel está comenzando!"
                            } else {
                                "$sessionLabel is starting now!"
                            }
                            showNotification(
                                id = notifKey.hashCode() + 1,
                                title = raceName,
                                text = text
                            )
                            prefs.edit().putBoolean(keyNow, true).apply()
                        }
                    }
                }
            }
        } catch (_: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

    private fun showNotification(id: Int, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        ensureNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .build()

        NotificationManagerCompat.from(applicationContext).notify(id, notification)
    }

    private fun ensureNotificationChannel() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "F1 Session Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for F1 session start times"
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "session_alerts"
    }
}
