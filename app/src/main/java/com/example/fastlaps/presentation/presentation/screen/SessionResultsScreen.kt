package com.example.fastlaps.presentation.presentation.screen

import EmptyState
import ErrorMessage
import LoadingIndicator
import QualifyingResult
import RaceResult
import RefreshButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.component.DriverPositionItem
import com.example.fastlaps.presentation.presentation.component.QualifyingPositionItem
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R

@Composable
fun SessionResultsScreen(
    raceResults: List<RaceResult>,
    qualifyingResults: List<QualifyingResult>,
    sprintResults: List<RaceResult>,
    raceName: String?,
    onBack: () -> Unit,
    viewModel: RaceViewModel
) {
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val noSessionMessage = stringResource(R.string.no_session)

    val hasTabs = listOf(raceResults, qualifyingResults, sprintResults).count { it.isNotEmpty() } > 1
    val hasSprint = sprintResults.isNotEmpty()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetRaceResults()
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
                        currentRound?.let { round ->
                            viewModel.loadRaceResults(round)
                        } ?: run {
                            viewModel.setErrorState(noSessionMessage)
                        }
                    }
                )
            }

            raceResults.isEmpty() && qualifyingResults.isEmpty() && sprintResults.isEmpty() -> {
                EmptyState(
                    onRetry = {
                        currentRound?.let { round ->
                            viewModel.loadRaceResults(round)
                        } ?: run {
                            viewModel.setErrorState(noSessionMessage)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = raceName ?: stringResource(R.string.results),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    ScalingLazyColumn(
                        modifier = Modifier.weight(1f),
                    ) {
                        if (hasTabs) {
                            if (raceResults.isNotEmpty()) {
                                item {
                                    TabChip(
                                        label = stringResource(R.string.race_label),
                                        selected = selectedTab == 0,
                                        onClick = { viewModel.selectTab(0) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (qualifyingResults.isNotEmpty()) {
                                item {
                                    TabChip(
                                        label = stringResource(R.string.qualifying),
                                        selected = selectedTab == 1,
                                        onClick = { viewModel.selectTab(1) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (hasSprint) {
                                item {
                                    TabChip(
                                        label = stringResource(R.string.sprint),
                                        selected = selectedTab == 2,
                                        onClick = { viewModel.selectTab(2) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = {
                                        currentRound?.let { round ->
                                            viewModel.loadRaceResults(round)
                                        } ?: run {
                                            viewModel.setErrorState(noSessionMessage)
                                        }
                                    },
                                    isErrorState = false,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        when (selectedTab) {
                            0 -> items(raceResults) { result ->
                                DriverPositionItem(
                                    result = result,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            1 -> items(qualifyingResults) { result ->
                                QualifyingPositionItem(
                                    result = result,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            2 -> items(sprintResults) { result ->
                                DriverPositionItem(
                                    result = result,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.caption2) },
        colors = if (selected) ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
        ) else ChipDefaults.secondaryChipColors(),
        modifier = modifier
    )
}
