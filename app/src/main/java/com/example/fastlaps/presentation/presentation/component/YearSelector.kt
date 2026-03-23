package com.example.fastlaps.presentation.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun YearSelector(
    selectedYear: Int,
    currentYear: Int,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val canGoBack = selectedYear > 2000
    val canGoForward = selectedYear < currentYear

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "◀",
            style = MaterialTheme.typography.body2,
            color = if (canGoBack) MaterialTheme.colors.secondary else Color.DarkGray,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(enabled = canGoBack) { onYearChange(selectedYear - 1) }
                .padding(8.dp)
        )

        Text(
            text = "$selectedYear",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = "▶",
            style = MaterialTheme.typography.body2,
            color = if (canGoForward) MaterialTheme.colors.secondary else Color.DarkGray,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(enabled = canGoForward) { onYearChange(selectedYear + 1) }
                .padding(8.dp)
        )
    }
}
