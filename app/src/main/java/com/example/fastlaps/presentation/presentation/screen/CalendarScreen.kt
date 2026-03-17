package com.example.fastlaps.presentation.presentation.screen

import Race
import RefreshButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.NavigateNext
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
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
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
import java.util.Locale

import EmptyState
import ErrorMessage
import LoadingIndicator

@Composable
fun CalendarScreen(
    viewModel: RaceViewModel,
    onRaceClick: (round: Int, raceName: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val races by viewModel.races.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    val today = LocalDate.now()
    val nextRace = races.firstOrNull {
        try { LocalDate.parse(it.date) >= today } catch (_: Exception) { false }
    }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                LoadingIndicator()
            }

            errorState != null -> {
                ErrorMessage(
                    errorState = errorState,
                    onRetry = { viewModel.loadRaces(forceRefresh = true) }
                )
            }

            races.isEmpty() -> {
                EmptyState(onRetry = { viewModel.loadRaces(forceRefresh = true) })
            }

            else -> {
                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                val season = races.firstOrNull()?.season ?: ""

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.calendar_title, season),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

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
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = { viewModel.loadRaces(forceRefresh = true) },
                                    isErrorState = false
                                )
                            }
                        }

                        items(races) { race ->
                            val isPast = try {
                                LocalDate.parse(race.date) < today
                            } catch (_: Exception) { false }

                            val isNext = race == nextRace

                            CalendarRaceItem(
                                race = race,
                                isPast = isPast,
                                isNext = isNext,
                                today = today,
                                onClick = if (isPast) {
                                    { onRaceClick(race.round.toInt(), race.raceName) }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarRaceItem(
    race: Race,
    isPast: Boolean,
    isNext: Boolean,
    today: LocalDate,
    onClick: (() -> Unit)?
) {
    val flag = F1Constants.countryFlag(race.Circuit.Location.country)
    val name = race.raceName.replace(" Grand Prix", "")
    val dateText = formatRaceDate(race.date)
    val timeText = formatLocalTime(race.date, race.time)
    val dateTimeText = if (timeText.isNotEmpty()) "$dateText · $timeText" else dateText

    val cardColor = when {
        isNext -> MaterialTheme.colors.secondary.copy(alpha = 0.25f)
        isPast -> MaterialTheme.colors.surface.copy(alpha = 0.5f)
        else -> MaterialTheme.colors.surface
    }

    val textAlpha = if (isPast && !isNext) 0.6f else 1f

    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp),
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = cardColor,
            endBackgroundColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status icon
            when {
                isNext -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = null,
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(18.dp)
                )
                isPast -> Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50).copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Race name with flag
                Text(
                    text = "$flag $name",
                    style = MaterialTheme.typography.body2,
                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colors.onSurface.copy(alpha = textAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Locality
                Text(
                    text = race.Circuit.Location.locality,
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = textAlpha * 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Date + time + status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = dateTimeText,
                        style = MaterialTheme.typography.caption2,
                        color = if (isNext) MaterialTheme.colors.secondary
                        else MaterialTheme.colors.onSurface.copy(alpha = textAlpha * 0.6f)
                    )

                    if (isNext) {
                        val raceDate = try { LocalDate.parse(race.date) } catch (_: Exception) { null }
                        if (raceDate != null) {
                            val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, raceDate)
                            val countdownText = when (daysUntil) {
                                0L -> stringResource(R.string.race_today)
                                1L -> stringResource(R.string.race_tomorrow)
                                else -> stringResource(R.string.race_in_days, daysUntil.toInt())
                            }
                            Text(
                                text = countdownText,
                                style = MaterialTheme.typography.caption2,
                                color = MaterialTheme.colors.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Round number
            Text(
                text = "R${race.round}",
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = textAlpha * 0.5f)
            )
        }
    }
}

private fun formatRaceDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        date.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
    } catch (_: Exception) {
        dateStr
    }
}

private fun formatLocalTime(dateStr: String, timeStr: String): String {
    if (timeStr.isEmpty()) return ""
    return try {
        val date = LocalDate.parse(dateStr)
        val utcTime = LocalTime.parse(timeStr.removeSuffix("Z"))
        val utcDateTime = ZonedDateTime.of(date, utcTime, ZoneId.of("UTC"))
        val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
        localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        ""
    }
}
