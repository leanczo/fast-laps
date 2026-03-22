package com.example.fastlaps.presentation.presentation.screen

import Race
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.SportsEsports
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val LightGreen = Color(0xFF00CC00)

@Composable
fun MainScreen(
    viewModel: RaceViewModel,
    onRacesClick: () -> Unit,
    onNextRaceClick: (round: Int) -> Unit,
    onAboutClick: () -> Unit,
    onPilotsClick: () -> Unit,
    onNewsClick: () -> Unit,
    onConstructorsClick: () -> Unit,
    onFastestLapsClick: () -> Unit,
    onReactionGameClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentLang: String,
    onLanguageChange: () -> Unit
) {
    val races by viewModel.races.collectAsState()
    val today = LocalDate.now()
    val nextRace = races.firstOrNull {
        try { LocalDate.parse(it.date) >= today } catch (_: Exception) { false }
    }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Scaffold(modifier = modifier.fillMaxSize()) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // App logo
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FastLaps",
                        style = MaterialTheme.typography.title3.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }

            // Next race with mini circuit map
            if (nextRace != null) {
                item {
                    NextRaceBanner(
                        race = nextRace,
                        today = today,
                        onClick = { onNextRaceClick(nextRace.round.toInt()) }
                    )
                }
            }

            // Menu items - 2 per row, no cards, just icons + text
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MenuItem(
                        icon = Icons.Default.DateRange,
                        label = stringResource(R.string.races),
                        onClick = onRacesClick
                    )
                    MenuItem(
                        icon = Icons.Default.EmojiEvents,
                        label = stringResource(R.string.drivers),
                        onClick = onPilotsClick
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MenuItem(
                        icon = Icons.Default.Groups,
                        label = stringResource(R.string.teams),
                        onClick = onConstructorsClick
                    )
                    MenuItem(
                        icon = Icons.Default.Timer,
                        label = stringResource(R.string.fastest_laps),
                        accentColor = Color(0xFF9B26B6),
                        onClick = onFastestLapsClick
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MenuItem(
                        icon = Icons.Default.NewReleases,
                        label = stringResource(R.string.news),
                        onClick = onNewsClick
                    )
                    MenuItem(
                        icon = Icons.Default.SportsEsports,
                        label = stringResource(R.string.reaction_game),
                        accentColor = LightGreen,
                        onClick = onReactionGameClick
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MenuItem(
                        icon = Icons.Default.Info,
                        label = stringResource(R.string.about),
                        onClick = onAboutClick
                    )
                    // Empty spacer to keep alignment
                    Spacer(modifier = Modifier.width(70.dp))
                }
            }

            // Language toggle
            item {
                val shape = RoundedCornerShape(10.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(shape)
                        .background(Color(0xFF1A1A1A))
                        .border(1.dp, Color(0xFF333333), shape)
                        .clickable(onClick = onLanguageChange)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(R.string.change_to)} ${
                            if (currentLang == "en") stringResource(R.string.spanish)
                            else stringResource(R.string.english)
                        }",
                        style = MaterialTheme.typography.caption2,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Version
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_version),
                        style = MaterialTheme.typography.caption3,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    Text(
                        text = stringResource(R.string.copyright),
                        style = MaterialTheme.typography.caption3,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    accentColor: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = accentColor.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 12.sp
        )
    }
}

@Composable
private fun NextRaceBanner(race: Race, today: LocalDate, onClick: () -> Unit) {
    val flag = F1Constants.countryFlag(race.Circuit.Location.country)
    val name = race.raceName.replace(" Grand Prix", "")
    val circuitRes = F1Constants.circuitDrawable(race.Circuit.circuitId)
    val raceDate = LocalDate.parse(race.date)
    val daysUntil = ChronoUnit.DAYS.between(today, raceDate)
    val countdownText = when (daysUntil) {
        0L -> stringResource(R.string.race_today)
        1L -> stringResource(R.string.race_tomorrow)
        else -> stringResource(R.string.race_in_days, daysUntil.toInt())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (circuitRes != 0) {
            Image(
                painter = painterResource(circuitRes),
                contentDescription = name,
                modifier = Modifier.size(36.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column {
            Text(
                text = "$flag $name",
                style = MaterialTheme.typography.caption2,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = countdownText,
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
