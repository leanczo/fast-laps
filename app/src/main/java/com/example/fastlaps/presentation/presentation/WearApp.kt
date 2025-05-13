package com.example.fastlaps.presentation.presentation

import AppNavigation
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fastlaps.presentation.domain.repository.SessionRepository

import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.theme.FastlapsTheme


@Composable
fun WearApp() {
    val viewModel: RaceViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                if (modelClass.isAssignableFrom(RaceViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return RaceViewModel(SessionRepository()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    FastlapsTheme {
        AppNavigation(viewModel)
    }
}