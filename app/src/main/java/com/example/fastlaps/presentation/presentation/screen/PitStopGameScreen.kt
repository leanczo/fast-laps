package com.example.fastlaps.presentation.presentation.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.leandro.fastlaps.R
import kotlinx.coroutines.delay

private val TireOff = Color(0xFF2A2A2A)
private val TireActive = Color(0xFFFF9800)
private val TireDone = Color(0xFF4CAF50)

private enum class PitStopState { READY, COUNTDOWN, PLAYING, RESULT }

@Composable
fun PitStopGameScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val gameState = remember { mutableStateOf(PitStopState.READY) }
    val countdownText = remember { mutableStateOf("") }
    val tireOrder = remember { mutableStateOf(listOf(0, 1, 2, 3).shuffled()) }
    val currentTireIndex = remember { mutableIntStateOf(-1) }
    val tireStates = remember { mutableStateOf(listOf(0, 0, 0, 0)) } // 0=off, 1=active, 2=done
    val startTime = remember { mutableLongStateOf(0L) }
    val totalTime = remember { mutableLongStateOf(0L) }
    val bestTime = remember { mutableLongStateOf(prefs.getLong("best_pit_stop_time", 0L)) }

    // Countdown sequence
    LaunchedEffect(gameState.value) {
        if (gameState.value == PitStopState.COUNTDOWN) {
            for (n in 3 downTo 1) {
                countdownText.value = "$n"
                delay(800)
            }
            countdownText.value = stringResource(context, R.string.pit_stop_go)
            delay(400)
            // Start game
            tireOrder.value = listOf(0, 1, 2, 3).shuffled()
            tireStates.value = listOf(0, 0, 0, 0)
            currentTireIndex.intValue = 0
            startTime.longValue = System.currentTimeMillis()
            // Activate first tire
            val first = tireOrder.value[0]
            tireStates.value = tireStates.value.toMutableList().also { it[first] = 1 }
            gameState.value = PitStopState.PLAYING
        }
    }

    fun onTireTap(tireIndex: Int) {
        if (gameState.value != PitStopState.PLAYING) return
        val idx = currentTireIndex.intValue
        if (idx < 0 || idx >= 4) return
        val expected = tireOrder.value[idx]
        if (tireIndex != expected) return

        // Mark tire done
        val newStates = tireStates.value.toMutableList()
        newStates[tireIndex] = 2

        val nextIdx = idx + 1
        if (nextIdx >= 4) {
            // All tires done
            tireStates.value = newStates
            totalTime.longValue = System.currentTimeMillis() - startTime.longValue
            if (bestTime.longValue == 0L || totalTime.longValue < bestTime.longValue) {
                bestTime.longValue = totalTime.longValue
                prefs.edit().putLong("best_pit_stop_time", totalTime.longValue).apply()
            }
            gameState.value = PitStopState.RESULT
        } else {
            // Activate next tire
            val next = tireOrder.value[nextIdx]
            newStates[next] = 1
            tireStates.value = newStates
            currentTireIndex.intValue = nextIdx
        }
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (gameState.value == PitStopState.READY || gameState.value == PitStopState.RESULT) {
                        Modifier.clickable {
                            gameState.value = PitStopState.COUNTDOWN
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                when (gameState.value) {
                    PitStopState.READY -> {
                        Text(
                            text = stringResource(R.string.pit_stop_title),
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            color = TireActive,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TireGrid(listOf(0, 0, 0, 0)) {}
                        Spacer(modifier = Modifier.height(8.dp))
                        if (bestTime.longValue > 0) {
                            val shape = RoundedCornerShape(8.dp)
                            Text(
                                text = "${stringResource(R.string.pit_stop_record)} ${formatTime(bestTime.longValue)}",
                                style = MaterialTheme.typography.caption2,
                                color = TireDone,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(shape)
                                    .background(Color(0xFF1A1A1A))
                                    .border(1.dp, Color(0xFF333333), shape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.pit_stop_clear_record),
                                style = MaterialTheme.typography.caption2,
                                color = Color(0xFFEF5350).copy(alpha = 0.7f),
                                modifier = Modifier
                                    .clickable {
                                        bestTime.longValue = 0L
                                        prefs.edit().remove("best_pit_stop_time").apply()
                                    }
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = stringResource(R.string.pit_stop_tap_start),
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    PitStopState.COUNTDOWN -> {
                        Text(
                            text = countdownText.value,
                            style = MaterialTheme.typography.title1.copy(fontSize = 36.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = TireActive
                        )
                    }

                    PitStopState.PLAYING -> {
                        Text(
                            text = stringResource(R.string.pit_stop_title),
                            style = MaterialTheme.typography.caption1,
                            color = TireActive,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TireGrid(tireStates.value) { onTireTap(it) }
                        Spacer(modifier = Modifier.height(6.dp))
                        val done = tireStates.value.count { it == 2 }
                        Text(
                            text = "$done/4",
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    PitStopState.RESULT -> {
                        val ms = totalTime.longValue
                        val resultColor = when {
                            ms < 2000 -> TireDone
                            ms < 3000 -> TireActive
                            else -> Color(0xFFEF5350)
                        }
                        val rating = when {
                            ms < 1500 -> "World Record!"
                            ms < 2000 -> "Red Bull Speed!"
                            ms < 2500 -> "Great Stop"
                            ms < 3500 -> "Average"
                            else -> "Slow Stop"
                        }

                        Text(
                            text = formatTime(ms),
                            style = MaterialTheme.typography.title1.copy(fontSize = 32.sp),
                            color = resultColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = rating,
                            style = MaterialTheme.typography.body2,
                            color = resultColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (bestTime.longValue > 0) {
                            Text(
                                text = "${stringResource(R.string.pit_stop_best)} ${formatTime(bestTime.longValue)}",
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.pit_stop_tap_retry),
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TireGrid(states: List<Int>, onTap: (Int) -> Unit) {
    // 2x2 grid: FL(0) FR(1) / RL(2) RR(3)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            TireCircle(state = states[0], onClick = { onTap(0) })
            TireCircle(state = states[1], onClick = { onTap(1) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            TireCircle(state = states[2], onClick = { onTap(2) })
            TireCircle(state = states[3], onClick = { onTap(3) })
        }
    }
}

@Composable
private fun TireCircle(state: Int, onClick: () -> Unit) {
    val color = when (state) {
        1 -> TireActive
        2 -> TireDone
        else -> TireOff
    }
    val borderColor = when (state) {
        1 -> TireActive
        2 -> TireDone
        else -> Color(0xFF444444)
    }
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (state == 0) 0.2f else 1f))
            .border(2.dp, borderColor, CircleShape)
            .clickable(enabled = state == 1, onClick = onClick)
    )
}

private fun formatTime(ms: Long): String {
    val seconds = ms / 1000
    val millis = ms % 1000
    return "${seconds}.${millis.toString().padStart(3, '0')}s"
}

private fun stringResource(context: Context, resId: Int): String {
    return context.getString(resId)
}
