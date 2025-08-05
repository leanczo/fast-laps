package com.example.fastlaps.presentation.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
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

    val positionColor = when (position.position) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colors.primary
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
            Text(
                text = "#${position.position}",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = positionColor
            )

            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
            ) {
                Text(
                    text = driverInfo?.broadcast_name ?: "Pilot ${position.driverNumber}",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color("#${driverInfo?.team_colour}".toColorInt()))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = driverInfo?.team_name ?: "",
                        style = MaterialTheme.typography.caption2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
