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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

@Composable
fun ConstructorDetailScreen(
    viewModel: RaceViewModel,
    constructorId: String,
    onHeadToHead: () -> Unit,
    onBack: () -> Unit
) {
    val constructor by viewModel.selectedConstructor.collectAsState()
    val drivers by viewModel.constructorDrivers.collectAsState()
    val seasonResults by viewModel.constructorSeasonResults.collectAsState()
    val isLoading by viewModel.isLoadingConstructorDetail.collectAsState()

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val lang = LocalConfiguration.current.locales[0].language

    LaunchedEffect(constructorId) {
        viewModel.loadConstructorDetail(constructorId)
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
            constructor?.let { standing ->
                val teamColor = F1Constants.teamColor(standing.Constructor.constructorId)
                val flag = F1Constants.nationalityFlag(standing.Constructor.nationality)

                // Team name
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        // Color dot + flag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(teamColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = flag,
                                style = MaterialTheme.typography.title3
                            )
                        }
                        Text(
                            text = standing.Constructor.name,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            color = teamColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                        )
                    }
                }

                // Stats row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge(value = "P${standing.position}", label = "Pos")
                        StatBadge(value = standing.points, label = "Pts")
                        StatBadge(
                            value = standing.wins,
                            label = if (lang == "es") "Vic" else "Wins"
                        )
                    }
                }

                // Drivers section
                if (drivers.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.drivers),
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(drivers) { driver ->
                        DriverChip(
                            name = "${driver.Driver.givenName} ${driver.Driver.familyName}",
                            flag = F1Constants.nationalityFlag(driver.Driver.nationality),
                            points = driver.points,
                            position = driver.position,
                            teamColor = teamColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Head to Head button
                if (drivers.size >= 2) {
                    item {
                        val shape = RoundedCornerShape(10.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clip(shape)
                                .background(teamColor.copy(alpha = 0.2f))
                                .border(1.dp, teamColor.copy(alpha = 0.5f), shape)
                                .clickable(onClick = onHeadToHead)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${drivers[0].Driver.code} vs ${drivers[1].Driver.code}",
                                style = MaterialTheme.typography.body2,
                                fontWeight = FontWeight.Bold,
                                color = teamColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = teamColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Season results
                item {
                    Text(
                        text = stringResource(R.string.season_results),
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                if (isLoading) {
                    item {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(seasonResults) { race ->
                        ConstructorRaceItem(
                            race = race,
                            teamColor = teamColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(value: String, label: String) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun DriverChip(
    name: String,
    flag: String,
    points: String,
    position: String,
    teamColor: Color,
    modifier: Modifier = Modifier
) {
    val positionColor = when (position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> Color.White
    }

    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(teamColor)
        )

        Text(
            text = "P$position",
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = positionColor,
            modifier = Modifier.padding(start = 10.dp, end = 6.dp)
        )

        Text(
            text = "$flag $name",
            style = MaterialTheme.typography.body2,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        )

        Text(
            text = "${points}pts",
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(end = 10.dp)
        )
    }
}

@Composable
private fun ConstructorRaceItem(
    race: Race,
    teamColor: Color,
    modifier: Modifier = Modifier
) {
    val flag = F1Constants.countryFlag(race.Circuit.Location.country)
    val name = race.raceName.replace(" Grand Prix", "")
    val results = race.Results.sortedBy { it.position.toIntOrNull() ?: 99 }

    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
    ) {
        // Race header with team color bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(teamColor)
            )
            Text(
                text = "$flag $name",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        // Each driver's result
        results.forEach { result ->
            val posColor = when (result.position) {
                "1" -> Color(0xFFFFD700)
                "2" -> Color(0xFFC0C0C0)
                "3" -> Color(0xFFCD7F32)
                else -> Color.White.copy(alpha = 0.7f)
            }
            val isFinished = result.status == "Finished"
            val isDNF = !isFinished && !result.status.startsWith("+") && result.status != "Lapped"
            val pts = result.points.toDoubleOrNull()?.let { if (it > 0) "+${result.points}" else null }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "P${result.position}",
                    style = MaterialTheme.typography.caption2,
                    fontWeight = FontWeight.Bold,
                    color = posColor,
                    modifier = Modifier.width(28.dp)
                )
                Text(
                    text = result.Driver.familyName,
                    style = MaterialTheme.typography.caption2,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isDNF) {
                    Text(
                        text = "DNF",
                        style = MaterialTheme.typography.caption2,
                        color = Color(0xFFEF5350)
                    )
                } else if (pts != null) {
                    Text(
                        text = pts,
                        style = MaterialTheme.typography.caption2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}
