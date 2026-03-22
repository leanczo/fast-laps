package com.example.fastlaps.presentation.presentation.component

import PitStop
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.util.F1Constants

@Composable
fun PitStopItem(
    driverName: String,
    constructorId: String,
    stops: List<PitStop>,
    modifier: Modifier = Modifier
) {
    val teamColor = F1Constants.teamColor(constructorId)
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
    ) {
        // Driver header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(teamColor)
            )
            Text(
                text = driverName,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, top = 6.dp, bottom = if (stops.isEmpty()) 6.dp else 2.dp)
            )
            Text(
                text = "${stops.size} stop${if (stops.size != 1) "s" else ""}",
                style = MaterialTheme.typography.caption2,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(end = 10.dp)
            )
        }

        // Each pit stop
        stops.forEach { stop ->
            val duration = stop.duration
            val durationSeconds = duration.toDoubleOrNull()
            val isFast = durationSeconds != null && durationSeconds < 25.0
            val durationColor = when {
                durationSeconds == null -> Color.White.copy(alpha = 0.7f)
                durationSeconds < 23.0 -> Color(0xFF4CAF50)
                durationSeconds < 26.0 -> Color.White.copy(alpha = 0.7f)
                durationSeconds < 30.0 -> Color(0xFFFFA726)
                else -> Color(0xFFEF5350)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stop ${stop.stop}",
                    style = MaterialTheme.typography.caption2,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.width(48.dp)
                )
                Text(
                    text = "Lap ${stop.lap}",
                    style = MaterialTheme.typography.caption2,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${duration}s",
                    style = MaterialTheme.typography.caption2,
                    fontWeight = FontWeight.Bold,
                    color = durationColor
                )
            }
        }
    }
}
