package com.example.fastlaps.presentation.presentation.component

import DriverStanding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.util.F1Constants

@Composable
fun DriverStandingItem(
    driver: DriverStanding,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val positionColor = when (driver.position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> Color.White
    }

    val teamColor = F1Constants.teamColor(driver.Constructors.firstOrNull()?.constructorId)
    val flag = F1Constants.nationalityFlag(driver.Driver.nationality)
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team color bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(teamColor)
        )

        // Position
        Text(
            text = driver.position,
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
                text = "$flag ${driver.Driver.familyName}",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = driver.Constructors.firstOrNull()?.name ?: "",
                style = MaterialTheme.typography.caption2,
                color = Color.White.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Points
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 2.dp)
        ) {
            Text(
                text = driver.points,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "pts",
                style = MaterialTheme.typography.caption2,
                color = Color.White.copy(alpha = 0.4f)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(16.dp).padding(end = 4.dp)
        )
    }
}
