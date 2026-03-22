package com.example.fastlaps.presentation.presentation.screen

import EmptyState
import ErrorMessage
import LoadingIndicator
import PitStop
import QualifyingResult
import RaceResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.component.DriverPositionItem
import com.example.fastlaps.presentation.presentation.component.PitStopItem
import com.example.fastlaps.presentation.presentation.component.QualifyingPositionItem
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

@Composable
fun SessionResultsScreen(
    raceResults: List<RaceResult>,
    qualifyingResults: List<QualifyingResult>,
    sprintResults: List<RaceResult>,
    raceName: String?,
    onBack: () -> Unit,
    onReplayClick: () -> Unit,
    viewModel: RaceViewModel
) {
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val circuitId by viewModel.currentCircuitId.collectAsState()
    val pitStops by viewModel.pitStops.collectAsState()

    val noSessionMessage = stringResource(R.string.no_session)

    val hasSprint = sprintResults.isNotEmpty()
    val hasPitStops = pitStops.isNotEmpty()

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

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
                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .onRotaryScrollEvent { event ->
                            coroutineScope.launch { listState.scrollBy(event.verticalScrollPixels) }
                            true
                        }
                        .focusRequester(focusRequester)
                        .focusable(),
                ) {
                    // Circuit map + race name
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val circuitRes = circuitId?.let { F1Constants.circuitDrawable(it) } ?: 0
                            if (circuitRes != 0) {
                                Image(
                                    painter = painterResource(circuitRes),
                                    contentDescription = raceName,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Text(
                                text = raceName ?: stringResource(R.string.results),
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Session buttons - Row 1: sessions
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (raceResults.isNotEmpty()) {
                                SessionIconButton(
                                    icon = Icons.Default.EmojiEvents,
                                    label = stringResource(R.string.race_label),
                                    selected = selectedTab == 0,
                                    onClick = { viewModel.selectTab(0) }
                                )
                            }
                            if (qualifyingResults.isNotEmpty()) {
                                SessionIconButton(
                                    icon = Icons.Default.Timer,
                                    label = stringResource(R.string.qualifying),
                                    selected = selectedTab == 1,
                                    onClick = { viewModel.selectTab(1) }
                                )
                            }
                            if (hasSprint) {
                                SessionIconButton(
                                    icon = Icons.Default.FlashOn,
                                    label = stringResource(R.string.sprint),
                                    selected = selectedTab == 2,
                                    onClick = { viewModel.selectTab(2) }
                                )
                            }
                        }
                    }

                    // Session buttons - Row 2: extras
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (hasPitStops) {
                                SessionIconButton(
                                    icon = Icons.Default.Build,
                                    label = "Pits",
                                    selected = selectedTab == 3,
                                    onClick = { viewModel.selectTab(3) }
                                )
                            }
                            if (raceResults.isNotEmpty()) {
                                SessionIconButton(
                                    icon = Icons.Default.PlayArrow,
                                    label = "Replay",
                                    selected = false,
                                    onClick = onReplayClick
                                )
                            }
                            SessionIconButton(
                                icon = Icons.Default.Refresh,
                                label = null,
                                selected = false,
                                onClick = {
                                    currentRound?.let { round ->
                                        viewModel.loadRaceResults(round)
                                    } ?: run {
                                        viewModel.setErrorState(noSessionMessage)
                                    }
                                }
                            )
                        }
                    }

                    // Results based on selected tab
                    when (selectedTab) {
                        0 -> items(raceResults) { result ->
                            DriverPositionItem(
                                result = result,
                                hasFastestLap = result.FastestLap?.rank == "1",
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
                                hasFastestLap = result.FastestLap?.rank == "1",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        3 -> {
                            // Group pit stops by driver
                            val grouped = pitStops.groupBy { it.driverId }
                            val driverOrder = raceResults.map { it.Driver.driverId }
                            val sortedDrivers = grouped.keys.sortedBy { id ->
                                driverOrder.indexOf(id).let { if (it == -1) 999 else it }
                            }
                            items(sortedDrivers) { driverId ->
                                val driverResult = raceResults.find { it.Driver.driverId == driverId }
                                val driverName = driverResult?.Driver?.familyName ?: driverId
                                val constructorId = driverResult?.Constructor?.constructorId ?: ""
                                PitStopItem(
                                    driverName = driverName,
                                    constructorId = constructorId,
                                    stops = grouped[driverId] ?: emptyList(),
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
private fun SessionIconButton(
    icon: ImageVector,
    label: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(36.dp),
            colors = if (selected) ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            ) else ButtonDefaults.secondaryButtonColors()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.caption2,
                color = if (selected) MaterialTheme.colors.secondary
                else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
