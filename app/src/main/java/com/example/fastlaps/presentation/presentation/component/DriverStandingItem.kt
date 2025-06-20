package com.example.fastlaps.presentation.presentation.component

import DriverStanding
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
fun DriverStandingItem(
    driver: DriverStanding,
    modifier: Modifier = Modifier
) {
    val driverPosition = driver.position
    val positionColor = when (driverPosition) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> MaterialTheme.colors.primary
    }

    fun getTeamColor(constructorId: String?): Color {
        return when (constructorId?.lowercase()) {
            "mclaren" -> Color(0xFFF47600)
            "red_bull" -> Color(0xFF4781D7)
            "mercedes" -> Color(0xFF00D7B6)
            "ferrari" -> Color(0xFFED1131)
            "aston_martin" -> Color(0xFF229971)
            "alpine" -> Color(0xFF00A1E8)
            "williams" -> Color(0xFF1868DB)
            "haas" -> Color(0xFF9C9FA2)
            "rb", -> Color(0xFF6C98FF)
            "sauber" -> Color(0xFF01C00E)
            else -> Color.Gray
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colors.onSurface,
        onClick = {  }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Position
            Text(
                text = driver.position,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = positionColor
            )

            // Driver info - Column ahora está correctamente alineado
            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${driver.Driver.givenName} ${driver.Driver.familyName}",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Círculo de color
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(getTeamColor(driver.Constructors.firstOrNull()?.constructorId))
                    )
                    // Nombre del equipo
                    Text(
                        text = driver.Constructors.firstOrNull()?.name ?: "",
                        style = MaterialTheme.typography.caption2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Points
            Text(
                text = driver.points,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(0.3f),
                textAlign = TextAlign.End
            )
        }
    }
}