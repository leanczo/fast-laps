package com.example.fastlaps.presentation.presentation.screen

import RefreshButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.presentation.component.DriverPositionItem
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import androidx.compose.runtime.getValue

@Composable
fun SessionResultsScreen(
    finalPositions: List<FinalPosition>,
    onBack: () -> Unit,
    viewModel: RaceViewModel
) {
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentSessionKey by viewModel.currentSessionKey.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSessionResults()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Positions",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Manejo de estados
            when {
                errorState != null -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorState ?: "Error loading data",
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                            onClick = {
                                currentSessionKey?.let { sessionKey ->
                                    viewModel.loadSessionData(sessionKey)
                                } ?: run {
                                    viewModel.setErrorState("No session selected")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error,
                                contentColor = MaterialTheme.colors.onError
                            ),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                isLoading && finalPositions.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            indicatorColor = MaterialTheme.colors.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                finalPositions.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No data available",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                else -> {
                    ScalingLazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(), // ocupa todo el ancho
                                contentAlignment = Alignment.Center // centra el contenido
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = {
                                        currentSessionKey?.let { sessionKey ->
                                            viewModel.loadSessionData(sessionKey)
                                        } ?: run {
                                            viewModel.setErrorState("No session selected")
                                        }
                                    },
                                    isErrorState = errorState != null,
                                    modifier = Modifier.size(40.dp) // Tamaño compacto pero táctil
                                )
                            }
                        }
                        items(finalPositions) { position ->
                            DriverPositionItem(
                                position = position,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}