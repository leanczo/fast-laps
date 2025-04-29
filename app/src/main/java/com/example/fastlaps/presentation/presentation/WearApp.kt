package com.example.fastlaps.presentation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.theme.FastlapsTheme

@Composable
fun WearApp() {
    val viewModel = remember { RaceViewModel() }
    val results by viewModel.results.collectAsState()

    FastlapsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.TopCenter
        ) {
            if (results.isEmpty()) {
                Text("Cargando...", color = MaterialTheme.colors.primary)
            } else {
                androidx.wear.compose.material.ScalingLazyColumn {
                    items(results.size) { index ->
                        val result = results[index]
                        Text(
                            text = "${result.position}. ${result.Driver.givenName} ${result.Driver.familyName} (${result.Constructor.name})",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}
