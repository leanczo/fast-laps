package com.example.fastlaps.presentation.presentation.component

import RaceResult
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.util.F1Constants
import kotlin.math.abs

private val F1Purple = Color(0xFF9B26B6)

private fun translateStatus(status: String, lang: String): String {
    if (lang != "es") return status
    return when (status) {
        "Finished" -> "Finalizado"
        "Disqualified" -> "Descalificado"
        "Accident" -> "Accidente"
        "Collision" -> "Colisión"
        "Engine" -> "Motor"
        "Gearbox" -> "Caja de cambios"
        "Transmission" -> "Transmisión"
        "Clutch" -> "Embrague"
        "Hydraulics" -> "Hidráulica"
        "Electrical" -> "Eléctrico"
        "Spun off" -> "Trompo"
        "Retired" -> "Retirado"
        "Suspension" -> "Suspensión"
        "Brakes" -> "Frenos"
        "Overheating" -> "Sobrecalentamiento"
        "Ignition" -> "Encendido"
        "Throttle" -> "Acelerador"
        "Turbo" -> "Turbo"
        "Lapped" -> "Doblado"
        "Not classified" -> "No clasificado"
        "Withdrew" -> "Se retiró"
        "Fuel system" -> "Combustible"
        "Oil leak" -> "Fuga de aceite"
        "Out of fuel" -> "Sin combustible"
        "Water leak" -> "Fuga de agua"
        "Puncture" -> "Pinchazo"
        "Power Unit" -> "Unidad de potencia"
        "Damage" -> "Daño"
        "Wheel" -> "Rueda"
        "Did not start" -> "No largó"
        "+1 Lap" -> "+1 Vuelta"
        else -> {
            val lapsMatch = Regex("^\\+(\\d+) Laps$").find(status)
            if (lapsMatch != null) "+${lapsMatch.groupValues[1]} Vueltas"
            else status
        }
    }
}

@Composable
fun DriverPositionItem(
    result: RaceResult,
    hasFastestLap: Boolean = false,
    modifier: Modifier = Modifier
) {
    val positionColor = when (result.position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> Color.White
    }

    val teamColor = F1Constants.teamColor(result.Constructor.constructorId)
    val lang = LocalConfiguration.current.locales[0].language
    val isFinished = result.status == "Finished"
    val isLapped = result.status.startsWith("+") || result.status == "Lapped"
    val statusText = translateStatus(result.status, lang)

    val statusColor = when {
        isFinished -> Color.Transparent
        isLapped -> MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
        else -> Color(0xFFEF5350)
    }

    val gridPos = result.grid.toIntOrNull()
    val finishPos = result.position.toIntOrNull()
    val gridChange = if (gridPos != null && finishPos != null && gridPos > 0) {
        gridPos - finishPos
    } else null

    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team color bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(teamColor)
        )

        // Position number
        Text(
            text = result.position,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = positionColor,
            modifier = Modifier.padding(start = 10.dp, end = 6.dp)
        )

        // Driver info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = result.Driver.familyName,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = result.Constructor.name,
                style = MaterialTheme.typography.caption2,
                color = Color.White.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Status / grid change / fastest lap
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 10.dp)
        ) {
            if (gridChange != null && gridChange != 0) {
                Text(
                    text = "${if (gridChange > 0) "▲" else "▼"}${abs(gridChange)}",
                    style = MaterialTheme.typography.caption2,
                    color = if (gridChange > 0) Color(0xFF4CAF50) else Color(0xFFEF5350),
                    fontWeight = FontWeight.Bold
                )
            }
            if (!isFinished) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.caption2,
                    color = statusColor,
                    maxLines = 1
                )
            }
            if (hasFastestLap) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(F1Purple, CircleShape)
                    )
                    Text(
                        text = result.FastestLap?.Time?.time ?: "FL",
                        style = MaterialTheme.typography.caption2,
                        fontWeight = FontWeight.Bold,
                        color = F1Purple
                    )
                }
            }
        }
    }
}
