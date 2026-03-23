package com.example.fastlaps.presentation.presentation.screen

import LoadingIndicator
import Race
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.component.YearSelector
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch
import kotlin.math.abs

private data class DriverEvo(
    val driverId: String,
    val code: String,
    val constructorId: String
)

private data class DriverRoundEntry(
    val driver: DriverEvo,
    val cumulativePoints: Int,
    val position: Int,
    val positionChange: Int // positive = gained positions, negative = lost
)

private data class RoundEvolution(
    val round: String,
    val raceName: String,
    val country: String,
    val entries: List<DriverRoundEntry>
)

@Composable
fun ChampionshipEvolutionScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit
) {
    val allResults by viewModel.allSeasonResults.collectAsState()
    val isLoading by viewModel.isLoadingFastestLaps.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    LaunchedEffect(selectedYear) {
        viewModel.loadAllSeasonResults()
    }

    val rounds = remember(allResults) { buildEvolution(allResults) }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else {
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.championship_evolution),
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    item {
                        YearSelector(
                            selectedYear = selectedYear,
                            currentYear = viewModel.currentYear,
                            onYearChange = { viewModel.setSelectedYear(it) }
                        )
                    }

                    if (rounds.isEmpty() && !isLoading) {
                        item {
                            Text(
                                text = stringResource(R.string.no_data),
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    items(rounds) { round ->
                        RoundCard(
                            round = round,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundCard(
    round: RoundEvolution,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val flag = F1Constants.countryFlag(round.country)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        // Race header
        Text(
            text = "$flag R${round.round} ${round.raceName}",
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
        )

        // Driver standings
        round.entries.forEach { entry ->
            DriverEvolutionRow(entry)
        }
    }
}

@Composable
private fun DriverEvolutionRow(entry: DriverRoundEntry) {
    val teamColor = F1Constants.teamColor(entry.driver.constructorId)
    val isLeader = entry.position == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team color bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(teamColor, RoundedCornerShape(1.dp))
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Position
        Text(
            text = "${entry.position}",
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = when (entry.position) {
                1 -> Color(0xFFFFD700)
                2 -> Color(0xFFC0C0C0)
                3 -> Color(0xFFCD7F32)
                else -> Color.White.copy(alpha = 0.5f)
            },
            modifier = Modifier.width(14.dp)
        )

        // Driver code
        Text(
            text = entry.driver.code,
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = teamColor,
            modifier = Modifier.width(34.dp)
        )

        // Points
        Text(
            text = "${entry.cumulativePoints}",
            style = MaterialTheme.typography.caption2,
            fontWeight = if (isLeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isLeader) Color(0xFFFFD700) else Color.White,
            modifier = Modifier.weight(1f)
        )

        // Position change
        if (entry.positionChange != 0) {
            val arrow = if (entry.positionChange > 0) "▲" else "▼"
            val color = if (entry.positionChange > 0) Color(0xFF4CAF50) else Color(0xFFEF5350)
            Text(
                text = "$arrow${abs(entry.positionChange)}",
                style = MaterialTheme.typography.caption2,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

private fun buildEvolution(allResults: List<Race>): List<RoundEvolution> {
    if (allResults.isEmpty()) return emptyList()

    // Collect total points to determine top 5 overall
    val totalPoints = mutableMapOf<String, Double>()
    val driverCodes = mutableMapOf<String, String>()
    val driverConstructors = mutableMapOf<String, String>()

    allResults.forEach { race ->
        race.Results.forEach { result ->
            val id = result.Driver.driverId
            totalPoints[id] = (totalPoints[id] ?: 0.0) + (result.points.toDoubleOrNull() ?: 0.0)
            driverCodes[id] = result.Driver.code.ifEmpty {
                result.Driver.familyName.take(3).uppercase()
            }
            driverConstructors[id] = result.Constructor.constructorId
        }
    }

    val top5Ids = totalPoints.entries.sortedByDescending { it.value }.take(5).map { it.key }.toSet()
    val drivers = top5Ids.map { id ->
        DriverEvo(id, driverCodes[id] ?: "", driverConstructors[id] ?: "")
    }

    // Build cumulative points and positions round by round
    val cumulative = top5Ids.associateWith { 0.0 }.toMutableMap()
    var prevPositions = emptyMap<String, Int>()

    return allResults.map { race ->
        // Add this round's points
        race.Results.forEach { result ->
            if (result.Driver.driverId in top5Ids) {
                cumulative[result.Driver.driverId] =
                    (cumulative[result.Driver.driverId] ?: 0.0) + (result.points.toDoubleOrNull() ?: 0.0)
            }
        }

        // Sort by cumulative points to get current positions
        val sorted = drivers.sortedByDescending { cumulative[it.driverId] ?: 0.0 }
        val currentPositions = sorted.mapIndexed { index, d -> d.driverId to (index + 1) }.toMap()

        val entries = sorted.mapIndexed { index, driver ->
            val pos = index + 1
            val prevPos = prevPositions[driver.driverId] ?: pos
            DriverRoundEntry(
                driver = driver,
                cumulativePoints = (cumulative[driver.driverId] ?: 0.0).toInt(),
                position = pos,
                positionChange = prevPos - pos // positive = gained positions
            )
        }

        prevPositions = currentPositions

        RoundEvolution(
            round = race.round,
            raceName = race.raceName.replace(" Grand Prix", ""),
            country = race.Circuit.Location.country,
            entries = entries
        )
    }
}
