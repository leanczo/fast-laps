package com.example.fastlaps.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fastlaps.presentation.notification.SessionNotificationWorker
import com.example.fastlaps.presentation.presentation.WearApp
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("language", Locale.getDefault().language) ?: "en"
        val updatedContext = setLocale(newBase, lang)
        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("language", Locale.getDefault().language) ?: "en"
        setLocale(this, lang)
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        createNotificationChannel()
        requestNotificationPermission()
        scheduleSessionNotifications()

        setContent {
            WearApp()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            SessionNotificationWorker.CHANNEL_ID,
            "F1 Session Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts for F1 session start times"
            enableVibration(true)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun scheduleSessionNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<SessionNotificationWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "session_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
