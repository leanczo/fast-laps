package com.example.fastlaps.presentation.presentation.screen

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.presentation.component.DriverPositionItem
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ButtonDefaults
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel

@Composable
fun SessionResultsScreen(
    finalPositions: List<FinalPosition>,
    onBack: () -> Unit,
    viewModel: RaceViewModel
) {

    DisposableEffect(Unit) {
        onDispose {
            // Esto se ejecutará cuando el composable se desmonte
            viewModel.resetSessionResults()
        }
    }

    Scaffold {
        if (finalPositions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading...", color = MaterialTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Título centrado
                Text(
                    text = "Positions", // o el título que prefieras
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .fillMaxWidth() // <-- También aplicable aquí
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                ScalingLazyColumn(
                    modifier = Modifier.weight(1f), // Ocupa todo el espacio restante
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    items(finalPositions) { position ->
                        DriverPositionItem(
                            position = position,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Footer con información de la app y desarrollador
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "v1.4", // Cambia esto por tu versión actual
                                style = MaterialTheme.typography.caption3,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "© 2025 Leandro Cardozo", // Reemplaza con tu nombre
                                style = MaterialTheme.typography.caption3,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}