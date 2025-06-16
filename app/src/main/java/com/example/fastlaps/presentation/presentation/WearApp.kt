package com.example.fastlaps.presentation.presentation

import AppNavigation
import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.theme.FastlapsTheme
import java.util.Locale


@Composable
fun WearApp() {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    // Estado para el idioma actual
    var currentLang by remember {
        mutableStateOf(prefs.getString("language", Locale.getDefault().language) ?: "en")
    }

    val viewModel: RaceViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                if (modelClass.isAssignableFrom(RaceViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return RaceViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    // Función para cambiar el idioma
    val toggleLanguage = {
        val newLang = if (currentLang == "en") "es" else "en"
        prefs.edit().putString("language", newLang).apply()
        currentLang = newLang
        // Reiniciar la actividad para aplicar los cambios
        (context as? Activity)?.recreate()
    }

    FastlapsTheme {
        AppNavigation(viewModel, currentLang, toggleLanguage as () -> Unit)
    }
}