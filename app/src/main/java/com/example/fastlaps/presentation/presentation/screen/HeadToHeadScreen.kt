package com.example.fastlaps.presentation.presentation.screen

import DriverStanding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import kotlinx.coroutines.launch

@Composable
fun HeadToHeadScreen(
    viewModel: RaceViewModel,
    constructorId: String,
    onBack: () -> Unit
) {
    val drivers by viewModel.constructorDrivers.collectAsState()
    val driver1Results by viewModel.h2hDriver1Results.collectAsState()
    val driver2Results by viewModel.h2hDriver2Results.collectAsState()
    val isLoading by viewModel.isLoadingH2H.collectAsState()

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(constructorId) {
        viewModel.loadHeadToHead(constructorId)
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val teamColor = F1Constants.teamColor(constructorId)
    val d1 = drivers.getOrNull(0)
    val d2 = drivers.getOrNull(1)

    Scaffold {
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
            if (d1 != null && d2 != null) {
                // Header: Driver 1 vs Driver 2
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = "Head to Head",
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = d1.Driver.code.ifEmpty { d1.Driver.familyName.take(3).uppercase() },
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                color = teamColor
                            )
                            Text(
                                text = "vs",
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                            Text(
                                text = d2.Driver.code.ifEmpty { d2.Driver.familyName.take(3).uppercase() },
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                color = teamColor
                            )
                        }
                    }
                }

                // Stats comparison
                val raceWins = countWins(driver1Results, driver2Results, d1.Driver.driverId, d2.Driver.driverId)

                item {
                    ComparisonRow(
                        label = "Qualifying",
                        left = countQualyWins(driver1Results, driver2Results, d1.Driver.driverId, d2.Driver.driverId).first.toString(),
                        right = countQualyWins(driver1Results, driver2Results, d1.Driver.driverId, d2.Driver.driverId).second.toString(),
                        teamColor = teamColor
                    )
                }
                item {
                    ComparisonRow(
                        label = "Race",
                        left = raceWins.first.toString(),
                        right = raceWins.second.toString(),
                        teamColor = teamColor
                    )
                }
                item {
                    ComparisonRow(
                        label = "Points",
                        left = d1.points,
                        right = d2.points,
                        teamColor = teamColor
                    )
                }
                item {
                    ComparisonRow(
                        label = "Wins",
                        left = d1.wins,
                        right = d2.wins,
                        teamColor = teamColor
                    )
                }

                if (isLoading) {
                    item {
                        LoadingIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
                    }
                } else {
                    // Race-by-race comparison
                    item {
                        Text(
                            text = "Race by Race",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    val allRounds = (driver1Results.map { it.round } + driver2Results.map { it.round }).distinct().sorted()

                    items(allRounds) { round ->
                        val r1 = driver1Results.find { it.round == round }
                        val r2 = driver2Results.find { it.round == round }
                        val raceName = (r1 ?: r2)?.raceName?.replace(" Grand Prix", "") ?: "R$round"
                        val country = (r1 ?: r2)?.Circuit?.Location?.country ?: ""
                        val flag = F1Constants.countryFlag(country)
                        val pos1 = r1?.Results?.firstOrNull()?.position ?: "-"
                        val pos2 = r2?.Results?.firstOrNull()?.position ?: "-"

                        RaceComparisonRow(
                            raceName = "$flag $raceName",
                            pos1 = pos1,
                            pos2 = pos2,
                            teamColor = teamColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun countWins(
    d1Races: List<Race>, d2Races: List<Race>,
    d1Id: String, d2Id: String
): Pair<Int, Int> {
    var w1 = 0; var w2 = 0
    val allRounds = (d1Races.map { it.round } + d2Races.map { it.round }).distinct()
    allRounds.forEach { round ->
        val p1 = d1Races.find { it.round == round }?.Results?.firstOrNull()?.position?.toIntOrNull() ?: 99
        val p2 = d2Races.find { it.round == round }?.Results?.firstOrNull()?.position?.toIntOrNull() ?: 99
        if (p1 < p2) w1++ else if (p2 < p1) w2++
    }
    return Pair(w1, w2)
}

private fun countQualyWins(
    d1Races: List<Race>, d2Races: List<Race>,
    d1Id: String, d2Id: String
): Pair<Int, Int> {
    var w1 = 0; var w2 = 0
    val allRounds = (d1Races.map { it.round } + d2Races.map { it.round }).distinct()
    allRounds.forEach { round ->
        val g1 = d1Races.find { it.round == round }?.Results?.firstOrNull()?.grid?.toIntOrNull() ?: 99
        val g2 = d2Races.find { it.round == round }?.Results?.firstOrNull()?.grid?.toIntOrNull() ?: 99
        if (g1 < g2) w1++ else if (g2 < g1) w2++
    }
    return Pair(w1, w2)
}

@Composable
private fun ComparisonRow(
    label: String,
    left: String,
    right: String,
    teamColor: Color
) {
    val shape = RoundedCornerShape(10.dp)
    val leftWins = (left.toDoubleOrNull() ?: 0.0) > (right.toDoubleOrNull() ?: 0.0)
    val rightWins = (right.toDoubleOrNull() ?: 0.0) > (left.toDoubleOrNull() ?: 0.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = left,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = if (leftWins) teamColor else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Center
        )
        Text(
            text = right,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = if (rightWins) teamColor else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RaceComparisonRow(
    raceName: String,
    pos1: String,
    pos2: String,
    teamColor: Color,
    modifier: Modifier = Modifier
) {
    val p1 = pos1.toIntOrNull() ?: 99
    val p2 = pos2.toIntOrNull() ?: 99
    val d1Won = p1 < p2
    val d2Won = p2 < p1

    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Driver 1 position
        Text(
            text = "P$pos1",
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = if (d1Won) teamColor else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center
        )

        // Race name
        Text(
            text = raceName,
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        // Driver 2 position
        Text(
            text = "P$pos2",
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = if (d2Won) teamColor else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center
        )
    }
}
