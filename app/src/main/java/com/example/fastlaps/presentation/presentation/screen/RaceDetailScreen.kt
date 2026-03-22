package com.example.fastlaps.presentation.presentation.screen

import Race
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
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun RaceDetailScreen(
    viewModel: RaceViewModel,
    round: Int,
    onBack: () -> Unit
) {
    val races by viewModel.races.collectAsState()
    val race = races.find { it.round == round.toString() }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Scaffold {
        if (race == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_data),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val circuitRes = F1Constants.circuitDrawable(race.Circuit.circuitId)
            val flag = F1Constants.countryFlag(race.Circuit.Location.country)
            val raceName = race.raceName.replace(" Grand Prix", "")
            val today = LocalDate.now()
            val raceDate = try { LocalDate.parse(race.date) } catch (_: Exception) { null }
            val daysUntil = raceDate?.let { ChronoUnit.DAYS.between(today, it) }

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
                // Circuit map
                if (circuitRes != 0) {
                    item {
                        Image(
                            painter = painterResource(circuitRes),
                            contentDescription = race.raceName,
                            modifier = Modifier
                                .size(110.dp)
                                .padding(top = 8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Race name + flag
                item {
                    Text(
                        text = "$flag $raceName",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                // Circuit name + location
                item {
                    Text(
                        text = "${race.Circuit.circuitName}\n${race.Circuit.Location.locality}, ${race.Circuit.Location.country}",
                        style = MaterialTheme.typography.caption2,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                }

                // Countdown
                if (daysUntil != null && daysUntil >= 0) {
                    item {
                        val countdownText = when (daysUntil) {
                            0L -> stringResource(R.string.race_today)
                            1L -> stringResource(R.string.race_tomorrow)
                            else -> stringResource(R.string.race_in_days, daysUntil.toInt())
                        }
                        Text(
                            text = countdownText,
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        )
                    }
                }

                // Session schedule
                item {
                    Text(
                        text = stringResource(R.string.schedule),
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                // Session rows
                race.FirstPractice?.let { session ->
                    item {
                        SessionRow(label = "FP1", date = session.date, time = session.time)
                    }
                }
                race.SecondPractice?.let { session ->
                    item {
                        SessionRow(label = "FP2", date = session.date, time = session.time)
                    }
                }
                race.ThirdPractice?.let { session ->
                    item {
                        SessionRow(label = "FP3", date = session.date, time = session.time)
                    }
                }
                race.Sprint?.let { session ->
                    item {
                        SessionRow(label = "Sprint", date = session.date, time = session.time)
                    }
                }
                race.Qualifying?.let { session ->
                    item {
                        SessionRow(
                            label = stringResource(R.string.qualifying),
                            date = session.date,
                            time = session.time
                        )
                    }
                }
                item {
                    SessionRow(
                        label = stringResource(R.string.race_label),
                        date = race.date,
                        time = race.time,
                        highlight = true
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionRow(
    label: String,
    date: String,
    time: String,
    highlight: Boolean = false
) {
    val formattedDate = try {
        val d = LocalDate.parse(date)
        d.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.getDefault()))
    } catch (_: Exception) { date }

    val localTime = if (time.isNotEmpty()) {
        try {
            val d = LocalDate.parse(date)
            val utcTime = LocalTime.parse(time.removeSuffix("Z"))
            val utcDateTime = ZonedDateTime.of(d, utcTime, ZoneId.of("UTC"))
            val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
            localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (_: Exception) { "" }
    } else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colors.secondary
            else MaterialTheme.colors.onSurface
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.caption2,
                color = if (highlight) MaterialTheme.colors.secondary
                else MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
            if (localTime.isNotEmpty()) {
                Text(
                    text = localTime,
                    style = MaterialTheme.typography.caption2,
                    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
                    color = if (highlight) MaterialTheme.colors.secondary
                    else MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
