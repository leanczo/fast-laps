package com.example.fastlaps.presentation.presentation.screen

import LoadingIndicator
import RaceResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R

@Composable
fun RaceReplayScreen(
    viewModel: RaceViewModel,
    round: Int,
    onBack: () -> Unit
) {
    val currentLapData by viewModel.currentLapData.collectAsState()
    val prevLapData by viewModel.prevLapData.collectAsState()
    val isLoading by viewModel.isLoadingLaps.collectAsState()
    val currentLap by viewModel.currentLap.collectAsState()
    val totalLaps by viewModel.totalLaps.collectAsState()
    val raceResults by viewModel.raceResults.collectAsState()
    val raceName by viewModel.currentRaceName.collectAsState()

    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(round) {
        viewModel.initReplay(round)
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val driverInfo = remember(raceResults) {
        raceResults.associate { it.Driver.driverId to it }
    }

    val prevPositions = remember(prevLapData) {
        prevLapData?.Timings?.associate { it.driverId to it.position.toIntOrNull() } ?: emptyMap()
    }

    Scaffold {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent { event ->
                    val delta = if (event.verticalScrollPixels > 0) 1 else -1
                    viewModel.changeLap(delta)
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        ) {
            // Header
            item {
                Text(
                    text = (raceName ?: "Race").replace(" Grand Prix", ""),
                    style = MaterialTheme.typography.caption2,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Lap counter with prev/next buttons
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.replay_lap),
                        style = MaterialTheme.typography.caption2,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = if (currentLap > 1) MaterialTheme.colors.secondary else Color(0xFF333333),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(enabled = currentLap > 1 && !isLoading) {
                                    viewModel.changeLap(-1)
                                }
                        )
                        Text(
                            text = "$currentLap",
                            style = MaterialTheme.typography.title2.copy(fontSize = 28.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (currentLap < totalLaps) MaterialTheme.colors.secondary else Color(0xFF333333),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(enabled = currentLap < totalLaps && !isLoading) {
                                    viewModel.changeLap(1)
                                }
                        )
                    }
                    // Progress bar
                    val progress = if (totalLaps > 0) currentLap.toFloat() / totalLaps else 0f
                    val barShape = RoundedCornerShape(2.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(4.dp)
                            .clip(barShape)
                            .background(Color(0xFF333333))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(MaterialTheme.colors.secondary)
                        )
                    }
                    Text(
                        text = "/ $totalLaps",
                        style = MaterialTheme.typography.caption2,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            if (isLoading) {
                item {
                    LoadingIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            } else if (currentLapData == null) {
                item {
                    Text(
                        text = stringResource(R.string.no_data),
                        style = MaterialTheme.typography.caption2,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                val sortedTimings = currentLapData!!.Timings.sortedBy { it.position.toIntOrNull() ?: 99 }

                items(sortedTimings) { timing ->
                    val result = driverInfo[timing.driverId]
                    val driverName = result?.Driver?.familyName ?: timing.driverId
                    val constructorId = result?.Constructor?.constructorId ?: ""
                    val teamColor = F1Constants.teamColor(constructorId)
                    val pos = timing.position.toIntOrNull() ?: 99
                    val prevPos = prevPositions[timing.driverId]
                    val posChange = if (prevPos != null) prevPos - pos else 0

                    ReplayPositionItem(
                        position = timing.position,
                        driverName = driverName,
                        lapTime = timing.time,
                        teamColor = teamColor,
                        posChange = posChange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReplayPositionItem(
    position: String,
    driverName: String,
    lapTime: String,
    teamColor: Color,
    posChange: Int,
    modifier: Modifier = Modifier
) {
    val positionColor = when (position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> Color.White
    }

    val shape = RoundedCornerShape(8.dp)

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
                .width(3.dp)
                .fillMaxHeight()
                .background(teamColor)
        )

        Text(
            text = position,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = positionColor,
            modifier = Modifier
                .width(24.dp)
                .padding(start = 6.dp),
            textAlign = TextAlign.End
        )

        if (posChange != 0) {
            val changeColor = if (posChange > 0) Color(0xFF4CAF50) else Color(0xFFEF5350)
            val icon = if (posChange > 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = changeColor,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(14.dp))
        }

        Text(
            text = driverName,
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp, top = 6.dp, bottom = 6.dp)
        )

        Text(
            text = lapTime,
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}
