package com.example.fastlaps.presentation.presentation.screen

import LoadingIndicator
import EmptyState
import ErrorMessage
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
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

private val F1Purple = Color(0xFF9B26B6)

data class FastestLapEntry(
    val driverName: String,
    val driverId: String,
    val constructorId: String,
    val nationality: String,
    val count: Int,
    val bestTime: String,
    val races: List<String>
)

@Composable
fun FastestLapsScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit
) {
    val races by viewModel.races.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    // Compute fastest laps from loaded race data
    val allResults by viewModel.allSeasonResults.collectAsState()
    val isLoadingFL by viewModel.isLoadingFastestLaps.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllSeasonResults()
    }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // Build fastest lap ranking
    val entries = remember(allResults) {
        val map = mutableMapOf<String, MutableList<Pair<String, String>>>() // driverId -> [(raceName, time)]
        val driverInfo = mutableMapOf<String, Triple<String, String, String>>() // driverId -> (name, constructorId, nationality)

        allResults.forEach { race ->
            race.Results.forEach { result ->
                if (result.FastestLap?.rank == "1") {
                    val id = result.Driver.driverId
                    val raceName = race.raceName.replace(" Grand Prix", "")
                    val time = result.FastestLap.Time?.time ?: ""
                    map.getOrPut(id) { mutableListOf() }.add(Pair(raceName, time))
                    driverInfo[id] = Triple(
                        result.Driver.familyName,
                        result.Constructor.constructorId,
                        result.Driver.nationality
                    )
                }
            }
        }

        map.entries.map { (id, raceList) ->
            val info = driverInfo[id]!!
            FastestLapEntry(
                driverName = info.first,
                driverId = id,
                constructorId = info.second,
                nationality = info.third,
                count = raceList.size,
                bestTime = raceList.minByOrNull { it.second }?.second ?: "",
                races = raceList.map { it.first }
            )
        }.sortedByDescending { it.count }
    }

    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingFL) {
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
                        .focusable()
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.fastest_laps),
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = F1Purple,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }

                    if (entries.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_data),
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    itemsIndexed(entries) { index, entry ->
                        FastestLapItem(
                            position = index + 1,
                            entry = entry,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FastestLapItem(
    position: Int,
    entry: FastestLapEntry,
    modifier: Modifier = Modifier
) {
    val teamColor = F1Constants.teamColor(entry.constructorId)
    val flag = F1Constants.nationalityFlag(entry.nationality)
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
        // Team color bar (purple tint)
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(F1Purple)
        )

        // Count (number of fastest laps)
        Text(
            text = "${entry.count}",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = F1Purple,
            modifier = Modifier.padding(start = 10.dp, end = 6.dp)
        )

        // Driver info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "$flag ${entry.driverName}",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.races.joinToString(", "),
                style = MaterialTheme.typography.caption2,
                color = Color.White.copy(alpha = 0.4f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Best time
        if (entry.bestTime.isNotEmpty()) {
            Text(
                text = entry.bestTime,
                style = MaterialTheme.typography.caption2,
                fontWeight = FontWeight.Bold,
                color = F1Purple,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}
