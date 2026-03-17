
import Race
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
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

@Composable
fun MainScreen(
    viewModel: RaceViewModel,
    onCircuitsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPilotsClick: () -> Unit,
    onNewsClick: () -> Unit,
    onConstructorsClick: () -> Unit,
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) {
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

            // Next race countdown
            if (nextRace != null) {
                item {
                    NextRaceCard(race = nextRace, today = today, onClick = onCircuitsClick)
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                Chip(
                    onClick = onCircuitsClick,
                    label = { Text(stringResource(R.string.races)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Chip(
                    onClick = onCalendarClick,
                    label = { Text(stringResource(R.string.calendar)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Chip(
                    onClick = onPilotsClick,
                    label = { Text(stringResource(R.string.drivers)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Chip(
                    onClick = onConstructorsClick,
                    label = { Text(stringResource(R.string.teams)) },
                    icon = { Icon(Icons.Default.Groups, null) },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Chip(
                    onClick = onNewsClick,
                    label = { Text(stringResource(R.string.news)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.NewReleases,
                            contentDescription = stringResource(R.string.news),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Chip(
                    onClick = onLanguageChange,
                    label = {
                        Text(
                            text = "${stringResource(R.string.change_to)} ${
                                if (currentLang == "en") stringResource(R.string.spanish)
                                else stringResource(R.string.english)
                            }",
                            style = MaterialTheme.typography.button
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = stringResource(R.string.change_language),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Chip(
                    onClick = onAboutClick,
                    label = { Text(stringResource(R.string.about)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.app_version),
                        style = MaterialTheme.typography.caption3,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(id = R.string.copyright),
                        style = MaterialTheme.typography.caption3,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun NextRaceCard(race: Race, today: LocalDate, onClick: () -> Unit) {
    val raceDate = LocalDate.parse(race.date)
    val daysUntil = ChronoUnit.DAYS.between(today, raceDate)
    val flag = F1Constants.countryFlag(race.Circuit.Location.country)
    val name = race.raceName.replace(" Grand Prix", "")

    val countdownText = when (daysUntil) {
        0L -> stringResource(R.string.race_today)
        1L -> stringResource(R.string.race_tomorrow)
        else -> stringResource(R.string.race_in_days, daysUntil.toInt())
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.next_race),
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "$flag $name",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = countdownText,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
