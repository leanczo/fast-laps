package com.example.fastlaps.presentation.presentation.component

import QualifyingResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun QualifyingPositionItem(
    result: QualifyingResult,
    modifier: Modifier = Modifier
) {
    val positionColor = when (result.position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> MaterialTheme.colors.primary
    }

    fun getTeamColor(constructorId: String): Color {
        return when (constructorId.lowercase()) {
            "mclaren" -> Color(0xFFF47600)
            "red_bull" -> Color(0xFF4781D7)
            "mercedes" -> Color(0xFF00D7B6)
            "ferrari" -> Color(0xFFED1131)
            "aston_martin" -> Color(0xFF229971)
            "alpine" -> Color(0xFF00A1E8)
            "williams" -> Color(0xFF1868DB)
            "haas" -> Color(0xFF9C9FA2)
            "rb" -> Color(0xFF6C98FF)
            "sauber" -> Color(0xFF01C00E)
            else -> Color.Gray
        }
    }

    val bestTime = when {
        result.Q3.isNotEmpty() -> result.Q3
        result.Q2.isNotEmpty() -> result.Q2
        result.Q1.isNotEmpty() -> result.Q1
        else -> ""
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colors.onSurface,
        onClick = { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#${result.position}",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = positionColor
            )

            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
            ) {
                Text(
                    text = "${result.Driver.givenName} ${result.Driver.familyName}",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(getTeamColor(result.Constructor.constructorId))
                    )
                    Text(
                        text = result.Constructor.name,
                        style = MaterialTheme.typography.caption2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (bestTime.isNotEmpty()) {
                Text(
                    text = bestTime,
                    style = MaterialTheme.typography.caption2,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
