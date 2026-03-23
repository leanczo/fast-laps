package com.example.fastlaps.presentation.presentation.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.delay

private val circuitNames = mapOf(
    "albert_park" to "Australia",
    "shanghai" to "China",
    "suzuka" to "Japan",
    "bahrain" to "Bahrain",
    "jeddah" to "Saudi Arabia",
    "miami" to "Miami",
    "imola" to "Emilia Romagna",
    "monaco" to "Monaco",
    "catalunya" to "Spain",
    "madring" to "Madrid",
    "villeneuve" to "Canada",
    "red_bull_ring" to "Austria",
    "silverstone" to "Great Britain",
    "spa" to "Belgium",
    "hungaroring" to "Hungary",
    "zandvoort" to "Netherlands",
    "monza" to "Italy",
    "baku" to "Azerbaijan",
    "marina_bay" to "Singapore",
    "americas" to "USA (COTA)",
    "rodriguez" to "Mexico",
    "interlagos" to "Brazil",
    "losail" to "Qatar",
    "yas_marina" to "Abu Dhabi",
    "las_vegas" to "Las Vegas",
    "vegas" to "Las Vegas"
)

private enum class GuessState { READY, QUESTION, FEEDBACK, RESULT }

private data class CircuitQuestion(
    val circuitId: String,
    val drawableRes: Int,
    val correctName: String,
    val options: List<String>,
    val correctIndex: Int
)

@Composable
fun CircuitGuessScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val gameState = remember { mutableStateOf(GuessState.READY) }
    val questions = remember { mutableStateOf(emptyList<CircuitQuestion>()) }
    val currentQ = remember { mutableIntStateOf(0) }
    val score = remember { mutableIntStateOf(0) }
    val selectedOption = remember { mutableIntStateOf(-1) }
    val bestScore = remember { mutableIntStateOf(prefs.getInt("best_circuit_guess_score", 0)) }

    fun generateQuestions(): List<CircuitQuestion> {
        val available = circuitNames.entries
            .filter { F1Constants.circuitDrawable(it.key) != 0 }
            .distinctBy { it.value }
        val shuffled = available.shuffled()
        val count = minOf(10, shuffled.size)
        val allNames = available.map { it.value }.distinct()

        return shuffled.take(count).map { (circuitId, correctName) ->
            val wrongs = (allNames - correctName).shuffled().take(2)
            val opts = (wrongs + correctName).shuffled()
            CircuitQuestion(
                circuitId = circuitId,
                drawableRes = F1Constants.circuitDrawable(circuitId),
                correctName = correctName,
                options = opts,
                correctIndex = opts.indexOf(correctName)
            )
        }
    }

    // Feedback auto-advance
    LaunchedEffect(gameState.value) {
        if (gameState.value == GuessState.FEEDBACK) {
            delay(1200)
            val next = currentQ.intValue + 1
            if (next >= questions.value.size) {
                if (score.intValue > bestScore.intValue) {
                    bestScore.intValue = score.intValue
                    prefs.edit().putInt("best_circuit_guess_score", score.intValue).apply()
                }
                gameState.value = GuessState.RESULT
            } else {
                currentQ.intValue = next
                selectedOption.intValue = -1
                gameState.value = GuessState.QUESTION
            }
        }
    }

    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                when (gameState.value) {
                    GuessState.READY -> {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                questions.value = generateQuestions()
                                currentQ.intValue = 0
                                score.intValue = 0
                                selectedOption.intValue = -1
                                gameState.value = GuessState.QUESTION
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.circuit_guess_title),
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF5350),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (bestScore.intValue > 0) {
                                    val shape = RoundedCornerShape(8.dp)
                                    Text(
                                        text = "${stringResource(R.string.circuit_guess_record)} ${bestScore.intValue}/10",
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
                                        text = stringResource(R.string.circuit_guess_clear_record),
                                        style = MaterialTheme.typography.caption2,
                                        color = Color(0xFFEF5350).copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .clickable {
                                                bestScore.intValue = 0
                                                prefs.edit().remove("best_circuit_guess_score").apply()
                                            }
                                            .padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = stringResource(R.string.circuit_guess_tap_start),
                                    style = MaterialTheme.typography.caption2,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    GuessState.QUESTION, GuessState.FEEDBACK -> {
                        if (questions.value.isNotEmpty()) {
                            val q = questions.value[currentQ.intValue]
                            val isFeedback = gameState.value == GuessState.FEEDBACK

                            // Header
                            Text(
                                text = "${currentQ.intValue + 1}/${questions.value.size}   ${stringResource(R.string.circuit_guess_best)} ${score.intValue}",
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.5f)
                            )

                            // Circuit image
                            Image(
                                painter = painterResource(q.drawableRes),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Options
                            q.options.forEachIndexed { index, name ->
                                val bgColor = when {
                                    !isFeedback -> Color(0xFF1A1A1A)
                                    index == q.correctIndex -> Color(0xFF2E7D32)
                                    index == selectedOption.intValue -> Color(0xFFC62828)
                                    else -> Color(0xFF1A1A1A)
                                }
                                val shape = RoundedCornerShape(8.dp)
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.caption2,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(shape)
                                        .background(bgColor)
                                        .border(1.dp, Color(0xFF333333), shape)
                                        .clickable(enabled = !isFeedback) {
                                            selectedOption.intValue = index
                                            if (index == q.correctIndex) {
                                                score.intValue++
                                            }
                                            gameState.value = GuessState.FEEDBACK
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    GuessState.RESULT -> {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                questions.value = generateQuestions()
                                currentQ.intValue = 0
                                score.intValue = 0
                                selectedOption.intValue = -1
                                gameState.value = GuessState.QUESTION
                            },
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
                                        text = "${stringResource(R.string.circuit_guess_best)} ${bestScore.intValue}/10",
                                        style = MaterialTheme.typography.caption2,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = stringResource(R.string.circuit_guess_tap_retry),
                                    style = MaterialTheme.typography.caption2,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
