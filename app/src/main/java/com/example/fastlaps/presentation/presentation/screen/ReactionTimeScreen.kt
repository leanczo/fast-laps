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
import androidx.compose.foundation.layout.fillMaxWidth
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

private val LightOff = Color(0xFF2A2A2A)
private val LightRed = Color(0xFFCC0000)
private val LightGreen = Color(0xFF00CC00)

enum class GameState {
    READY,      // Tap to start
    LIGHTS,     // Lights sequence
    WAIT,       // All lights on, waiting for blackout
    GO,         // Lights out! Tap now!
    RESULT,     // Show reaction time
    JUMP_START  // Tapped too early
}

@Composable
fun ReactionTimeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val gameState = remember { mutableStateOf(GameState.READY) }
    val lightsOn = remember { mutableIntStateOf(0) }
    val goTime = remember { mutableLongStateOf(0L) }
    val reactionMs = remember { mutableLongStateOf(0L) }
    val bestTime = remember { mutableLongStateOf(prefs.getLong("best_reaction_time", 0L)) }

    // Lights sequence
    LaunchedEffect(gameState.value) {
        if (gameState.value == GameState.LIGHTS) {
            lightsOn.intValue = 0
            for (i in 1..5) {
                delay(600)
                lightsOn.intValue = i
            }
            gameState.value = GameState.WAIT
        }
    }

    // Random delay before lights out
    LaunchedEffect(gameState.value) {
        if (gameState.value == GameState.WAIT) {
            val randomDelay = (1000L..4000L).random()
            delay(randomDelay)
            if (gameState.value == GameState.WAIT) {
                lightsOn.intValue = 0
                goTime.longValue = System.currentTimeMillis()
                gameState.value = GameState.GO
            }
        }
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    when (gameState.value) {
                        GameState.READY, GameState.RESULT, GameState.JUMP_START -> {
                            gameState.value = GameState.LIGHTS
                        }
                        GameState.LIGHTS, GameState.WAIT -> {
                            gameState.value = GameState.JUMP_START
                        }
                        GameState.GO -> {
                            reactionMs.longValue = System.currentTimeMillis() - goTime.longValue
                            if (bestTime.longValue == 0L || reactionMs.longValue < bestTime.longValue) {
                                bestTime.longValue = reactionMs.longValue
                                prefs.edit().putLong("best_reaction_time", reactionMs.longValue).apply()
                            }
                            gameState.value = GameState.RESULT
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                when (gameState.value) {
                    GameState.READY -> {
                        Text(
                            text = stringResource(R.string.reaction_title),
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LightsRow(count = 0)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (bestTime.longValue > 0) {
                            val shape = RoundedCornerShape(8.dp)
                            Text(
                                text = "${stringResource(R.string.reaction_record)} ${bestTime.longValue}ms",
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
                                text = stringResource(R.string.reaction_clear_record),
                                style = MaterialTheme.typography.caption2,
                                color = Color(0xFFEF5350).copy(alpha = 0.7f),
                                modifier = Modifier
                                    .clickable {
                                        bestTime.longValue = 0L
                                        prefs.edit().remove("best_reaction_time").apply()
                                    }
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = stringResource(R.string.reaction_tap_start),
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }

                    GameState.LIGHTS, GameState.WAIT -> {
                        Text(
                            text = stringResource(R.string.reaction_wait),
                            style = MaterialTheme.typography.body2,
                            color = LightRed,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LightsRow(count = lightsOn.intValue)
                    }

                    GameState.GO -> {
                        Text(
                            text = stringResource(R.string.reaction_go),
                            style = MaterialTheme.typography.title2,
                            color = LightGreen,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LightsRow(count = 0, allGreen = true)
                    }

                    GameState.RESULT -> {
                        val ms = reactionMs.longValue
                        val resultColor = when {
                            ms < 200 -> LightGreen
                            ms < 300 -> Color(0xFFFFA726)
                            else -> LightRed
                        }
                        val rating = when {
                            ms < 150 -> "Incredible!"
                            ms < 200 -> "Great!"
                            ms < 250 -> "Good"
                            ms < 350 -> "Average"
                            else -> "Slow"
                        }

                        Text(
                            text = "${ms}ms",
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
                            val shape = RoundedCornerShape(8.dp)
                            Text(
                                text = "${stringResource(R.string.reaction_best)} ${bestTime.longValue}ms",
                                style = MaterialTheme.typography.caption2,
                                color = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .clip(shape)
                                    .background(Color(0xFF1A1A1A))
                                    .border(1.dp, Color(0xFF333333), shape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.reaction_tap_retry),
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }

                    GameState.JUMP_START -> {
                        Text(
                            text = stringResource(R.string.reaction_jump_start),
                            style = MaterialTheme.typography.body1,
                            color = LightRed,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LightsRow(count = 5)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.reaction_tap_retry),
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
private fun LightsRow(count: Int, allGreen: Boolean = false) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val color = when {
                allGreen -> LightGreen
                i <= count -> LightRed
                else -> LightOff
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (color != LightOff) Modifier else
                            Modifier.border(1.dp, Color(0xFF444444), CircleShape)
                    )
            )
        }
    }
}
