/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.fastlaps.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fastlaps.presentation.presentation.WearApp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("language", Locale.getDefault().language) ?: "en"
        setLocale(this, lang)
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }

    private fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
