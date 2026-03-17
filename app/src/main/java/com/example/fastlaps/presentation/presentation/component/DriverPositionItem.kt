package com.example.fastlaps.presentation.presentation.component

import RaceResult
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.util.F1Constants

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
    modifier: Modifier = Modifier
) {
    val positionColor = when (result.position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> MaterialTheme.colors.primary
    }

    val lang = LocalConfiguration.current.locales[0].language
    val isFinished = result.status == "Finished"
    val isLapped = result.status.startsWith("+") || result.status == "Lapped"
    val statusText = translateStatus(result.status, lang)

    val statusColor = when {
        isFinished -> MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
        isLapped -> MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        else -> MaterialTheme.colors.error
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
                            .background(F1Constants.teamColor(result.Constructor.constructorId))
                    )
                    Text(
                        text = result.Constructor.name,
                        style = MaterialTheme.typography.caption2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
            }
        }
    }
}
