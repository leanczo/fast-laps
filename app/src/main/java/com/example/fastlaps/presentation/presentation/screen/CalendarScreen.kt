package com.example.fastlaps.presentation.presentation.screen

import EmptyState
import ErrorMessage
import LoadingIndicator
import Race
import RefreshButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.component.YearSelector
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: RaceViewModel,
    onRaceClick: (round: Int, raceName: String) -> Unit,
    onFutureRaceClick: (round: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val races by viewModel.races.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val today = now.toLocalDate()
    val nextRace = races.firstOrNull { race ->
        try {
            val raceDate = LocalDate.parse(race.date)
            when {
                raceDate > today -> true
                raceDate == today && race.time.isNotEmpty() ->
                    LocalDateTime.of(raceDate, LocalTime.parse(race.time.trimEnd('Z')))
                        .atOffset(ZoneOffset.UTC).isAfter(now)
                else -> false
            }
        } catch (_: Exception) { false }
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
                val raceRows = races.chunked(2)

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
                    // Year selector
                    item {
                        YearSelector(
                            selectedYear = selectedYear,
                            currentYear = viewModel.currentYear,
                            onYearChange = { viewModel.setSelectedYear(it) }
                        )
                    }

                    // Refresh
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

                    // Grid rows (2 per row)
                    items(raceRows) { rowRaces ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            rowRaces.forEach { race ->
                                val isPast = try {
                                    val raceDate = LocalDate.parse(race.date)
                                    when {
                                        raceDate < today -> true
                                        raceDate == today && race.time.isNotEmpty() ->
                                            LocalDateTime.of(raceDate, LocalTime.parse(race.time.trimEnd('Z')))
                                                .atOffset(ZoneOffset.UTC).isBefore(now)
                                        else -> false
                                    }
                                } catch (_: Exception) { false }
                                val isNext = race == nextRace

                                CircuitGridItem(
                                    race = race,
                                    isPast = isPast,
                                    isNext = isNext,
                                    today = today,
                                    onClick = if (isPast) {
                                        { onRaceClick(race.round.toInt(), race.raceName) }
                                    } else {
                                        { onFutureRaceClick(race.round.toInt()) }
                                    }
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
private fun CircuitGridItem(
    race: Race,
    isPast: Boolean,
    isNext: Boolean,
    today: LocalDate,
    onClick: () -> Unit
) {
    val circuitRes = F1Constants.circuitDrawable(race.Circuit.circuitId)
    val flag = F1Constants.countryFlag(race.Circuit.Location.country)
    val name = race.raceName.replace(" Grand Prix", "")
    val dateText = formatRaceDate(race.date)
    val itemAlpha = if (isPast) 0.5f else 1f

    val borderModifier = if (isNext) {
        Modifier.border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(85.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(borderModifier)
            .clickable(onClick = onClick)
            .alpha(itemAlpha)
            .padding(6.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            if (circuitRes != 0) {
                Image(
                    painter = painterResource(circuitRes),
                    contentDescription = name,
                    modifier = Modifier
                        .size(55.dp)
                        .padding(2.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(modifier = Modifier.size(55.dp))
            }
            if (isPast) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .size(14.dp)
                        .background(MaterialTheme.colors.background, CircleShape)
                )
            }
        }

        Text(
            text = "$flag $name",
            style = MaterialTheme.typography.caption2,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = dateText,
            style = MaterialTheme.typography.caption2,
            color = if (isNext) MaterialTheme.colors.secondary
            else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        if (isNext) {
            val raceDate = try { LocalDate.parse(race.date) } catch (_: Exception) { null }
            if (raceDate != null) {
                val daysUntil = ChronoUnit.DAYS.between(today, raceDate)
                val countdownText = when (daysUntil) {
                    0L -> stringResource(R.string.race_today)
                    1L -> stringResource(R.string.race_tomorrow)
                    else -> stringResource(R.string.race_in_days, daysUntil.toInt())
                }
                Text(
                    text = countdownText,
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
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
