package com.example.fastlaps.presentation.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.FinalPosition


@Composable
fun DriverPositionItem(
    position: FinalPosition,
    modifier: Modifier = Modifier
) {
    val driverInfo = position.driverInfo

    // Color basado en la posición
    val positionColor = when (position.position) {
        1 -> Color(0xFFFFD700) // Oro
        2 -> Color(0xFFC0C0C0) // Plata
        3 -> Color(0xFFCD7F32) // Bronce
        else -> MaterialTheme.colors.primary // Usa el color principal para otros
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp), // Armoniza el padding
        contentColor = MaterialTheme.colors.onSurface, // Contraste adecuado para texto
        onClick = { /* Acción al hacer clic */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Posición
            Text(
                text = "#${position.position}",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = positionColor
            )

            // Nombre del piloto y equipo
            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text(
                    text = driverInfo?.broadcast_name ?: "Piloto ${position.driverNumber}",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = driverInfo?.team_name ?: "",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
