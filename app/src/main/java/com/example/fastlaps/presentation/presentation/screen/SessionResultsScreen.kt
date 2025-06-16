package com.example.fastlaps.presentation.presentation.screen

import EmptyState
import ErrorMessage
import LoadingIndicator
import RefreshButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.presentation.component.DriverPositionItem
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R

@Composable
fun SessionResultsScreen(
    finalPositions: List<FinalPosition>,
    onBack: () -> Unit,
    viewModel: RaceViewModel
) {
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentSessionKey by viewModel.currentSessionKey.collectAsState()

    val no_session_message = stringResource(R.string.no_session);
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSessionResults()
        }
    }

    Scaffold {
        when {
            isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            errorState != null -> {
                ErrorMessage(
                    errorState = errorState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    onRetry = {
                        currentSessionKey?.let { sessionKey ->
                            viewModel.loadSessionData(sessionKey)
                        } ?: run {
                            viewModel.setErrorState(no_session_message)
                        }
                    }
                )
            }

            finalPositions.isEmpty() -> {
                EmptyState(
                    onRetry = {
                        currentSessionKey?.let { sessionKey ->
                            viewModel.loadSessionData(sessionKey)
                        } ?: run {
                            viewModel.setErrorState(no_session_message)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.results),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    ScalingLazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = {
                                        currentSessionKey?.let { sessionKey ->
                                            viewModel.loadSessionData(sessionKey)
                                        } ?: run {
                                            viewModel.setErrorState(no_session_message)
                                        }
                                    },
                                    isErrorState = false,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        items(finalPositions) { position ->
                            DriverPositionItem(
                                position = position,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}