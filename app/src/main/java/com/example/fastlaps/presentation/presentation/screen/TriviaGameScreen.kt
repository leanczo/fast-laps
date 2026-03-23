package com.example.fastlaps.presentation.presentation.screen

import LoadingIndicator
import Race
import DriverStanding
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val AccentBlue = Color(0xFF42A5F5)

private data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

private enum class TriviaState { LOADING, READY, QUESTION, FEEDBACK, RESULT }

@Composable
fun TriviaGameScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val allResults by viewModel.allSeasonResults.collectAsState()
    val standings by viewModel.driverStandings.collectAsState()
    val races by viewModel.races.collectAsState()
    val isLoadingResults by viewModel.isLoadingFastestLaps.collectAsState()
    val isLoadingDrivers by viewModel.isLoadingDrivers.collectAsState()

    val gameState = remember { mutableStateOf(TriviaState.LOADING) }
    val questions = remember { mutableStateOf(emptyList<TriviaQuestion>()) }
    val currentQ = remember { mutableIntStateOf(0) }
    val score = remember { mutableIntStateOf(0) }
    val selectedOption = remember { mutableIntStateOf(-1) }
    val bestScore = remember { mutableIntStateOf(prefs.getInt("best_trivia_score", 0)) }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.loadAllSeasonResults()
        viewModel.loadDriverStandings()
    }

    // Transition from LOADING to READY when data arrives
    LaunchedEffect(allResults, standings, isLoadingResults, isLoadingDrivers) {
        if (gameState.value == TriviaState.LOADING && !isLoadingResults && !isLoadingDrivers) {
            if (allResults.isNotEmpty() || standings.isNotEmpty()) {
                gameState.value = TriviaState.READY
            }
        }
    }

    // Feedback auto-advance
    LaunchedEffect(gameState.value) {
        if (gameState.value == TriviaState.FEEDBACK) {
            delay(1500)
            val next = currentQ.intValue + 1
            if (next >= questions.value.size) {
                if (score.intValue > bestScore.intValue) {
                    bestScore.intValue = score.intValue
                    prefs.edit().putInt("best_trivia_score", score.intValue).apply()
                }
                gameState.value = TriviaState.RESULT
            } else {
                currentQ.intValue = next
                selectedOption.intValue = -1
                gameState.value = TriviaState.QUESTION
            }
        }
    }

    fun startGame() {
        val generated = generateQuestions(allResults, standings, races)
        if (generated.isNotEmpty()) {
            questions.value = generated
            currentQ.intValue = 0
            score.intValue = 0
            selectedOption.intValue = -1
            gameState.value = TriviaState.QUESTION
        }
    }

    Scaffold {
        when (gameState.value) {
            TriviaState.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }

            TriviaState.READY -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { startGame() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.trivia_title),
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (bestScore.intValue > 0) {
                            val shape = RoundedCornerShape(8.dp)
                            Text(
                                text = "${stringResource(R.string.trivia_record)} ${bestScore.intValue}/10",
                                style = MaterialTheme.typography.caption2,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(shape)
                                    .background(Color(0xFF1A1A1A))
                                    .border(1.dp, Color(0xFF333333), shape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.trivia_clear_record),
                                style = MaterialTheme.typography.caption2,
                                color = Color(0xFFEF5350).copy(alpha = 0.7f),
                                modifier = Modifier
                                    .clickable {
                                        bestScore.intValue = 0
                                        prefs.edit().remove("best_trivia_score").apply()
                                    }
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = stringResource(R.string.trivia_tap_start),
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            TriviaState.QUESTION, TriviaState.FEEDBACK -> {
                if (questions.value.isNotEmpty() && currentQ.intValue < questions.value.size) {
                    val q = questions.value[currentQ.intValue]
                    val isFeedback = gameState.value == TriviaState.FEEDBACK

                    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
                        // Header
                        item {
                            Text(
                                text = "${currentQ.intValue + 1}/${questions.value.size}   ${score.intValue}pts",
                                style = MaterialTheme.typography.caption2,
                                color = AccentBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Question
                        item {
                            Text(
                                text = q.question,
                                style = MaterialTheme.typography.caption1,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Options
                        items(q.options.size) { index ->
                            val option = q.options[index]
                            val bgColor = when {
                                !isFeedback -> Color(0xFF1A1A1A)
                                index == q.correctIndex -> Color(0xFF2E7D32)
                                index == selectedOption.intValue -> Color(0xFFC62828)
                                else -> Color(0xFF1A1A1A)
                            }
                            val shape = RoundedCornerShape(8.dp)
                            Text(
                                text = option,
                                style = MaterialTheme.typography.caption2,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .clip(shape)
                                    .background(bgColor)
                                    .border(1.dp, Color(0xFF333333), shape)
                                    .clickable(enabled = !isFeedback) {
                                        selectedOption.intValue = index
                                        if (index == q.correctIndex) {
                                            score.intValue++
                                        }
                                        gameState.value = TriviaState.FEEDBACK
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            TriviaState.RESULT -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { startGame() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val resultColor = when {
                            score.intValue >= 9 -> Color(0xFF4CAF50)
                            score.intValue >= 6 -> Color(0xFFFF9800)
                            else -> Color(0xFFEF5350)
                        }
                        Text(
                            text = "${score.intValue}/${questions.value.size}",
                            style = MaterialTheme.typography.title1.copy(fontSize = 32.sp),
                            color = resultColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (bestScore.intValue > 0) {
                            Text(
                                text = "${stringResource(R.string.trivia_best)} ${bestScore.intValue}/10",
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.trivia_tap_retry),
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

private fun generateQuestions(
    allResults: List<Race>,
    standings: List<DriverStanding>,
    races: List<Race>
): List<TriviaQuestion> {
    val pool = mutableListOf<TriviaQuestion>()
    val allDriverNames = standings.map { it.Driver.familyName }.distinct()
    val allTeamNames = standings.map { it.Constructors.firstOrNull()?.name ?: "" }.filter { it.isNotEmpty() }.distinct()
    val allCountries = races.map { it.Circuit.Location.country }.filter { it.isNotEmpty() }.distinct()

    // Type 1: Who won the X GP?
    allResults.forEach { race ->
        val winner = race.Results.firstOrNull { it.position == "1" }
        if (winner != null && allDriverNames.size >= 4) {
            val correct = winner.Driver.familyName
            val wrongs = (allDriverNames - correct).shuffled().take(3)
            if (wrongs.size == 3) {
                val opts = (wrongs + correct).shuffled()
                val raceName = race.raceName.replace(" Grand Prix", " GP")
                pool.add(TriviaQuestion("Who won the $raceName?", opts, opts.indexOf(correct)))
            }
        }
    }

    // Type 2: Which team does X drive for?
    standings.forEach { standing ->
        val team = standing.Constructors.firstOrNull()?.name ?: return@forEach
        if (allTeamNames.size >= 4) {
            val wrongs = (allTeamNames - team).shuffled().take(3)
            if (wrongs.size == 3) {
                val opts = (wrongs + team).shuffled()
                pool.add(TriviaQuestion("${standing.Driver.familyName}'s team?", opts, opts.indexOf(team)))
            }
        }
    }

    // Type 3: Who leads the championship?
    if (standings.isNotEmpty() && allDriverNames.size >= 4) {
        val leader = standings.first().Driver.familyName
        val wrongs = (allDriverNames - leader).shuffled().take(3)
        if (wrongs.size == 3) {
            val opts = (wrongs + leader).shuffled()
            pool.add(TriviaQuestion("Who leads the championship?", opts, opts.indexOf(leader)))
        }
    }

    // Type 4: How many wins does X have?
    standings.filter { (it.wins.toIntOrNull() ?: 0) > 0 }.forEach { standing ->
        val wins = standing.wins.toIntOrNull() ?: return@forEach
        val wrongs = generateWrongNumbers(wins, 3)
        val opts = (wrongs.map { it.toString() } + wins.toString()).shuffled()
        pool.add(TriviaQuestion("Wins for ${standing.Driver.familyName}?", opts, opts.indexOf(wins.toString())))
    }

    // Type 5: Where is circuit X?
    races.filter { it.Circuit.circuitName.isNotEmpty() && allCountries.size >= 3 }.forEach { race ->
        val correct = race.Circuit.Location.country
        if (correct.isNotEmpty()) {
            val wrongs = (allCountries - correct).shuffled().take(3)
            if (wrongs.size == 3) {
                val opts = (wrongs + correct).shuffled()
                val circuitName = race.Circuit.circuitName
                pool.add(TriviaQuestion("Where is $circuitName?", opts, opts.indexOf(correct)))
            }
        }
    }

    return pool.shuffled().take(10)
}

private fun generateWrongNumbers(correct: Int, count: Int): List<Int> {
    val wrongs = mutableSetOf<Int>()
    val candidates = listOf(correct - 2, correct - 1, correct + 1, correct + 2, correct + 3)
        .filter { it >= 0 && it != correct }
    wrongs.addAll(candidates.shuffled().take(count))
    while (wrongs.size < count) {
        wrongs.add((0..20).random().let { if (it == correct) it + 1 else it })
    }
    return wrongs.take(count).toList()
}
