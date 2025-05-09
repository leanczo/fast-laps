package com.example.fastlaps.presentation.presentation.screen

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.presentation.component.DriverPositionItem
import androidx.compose.foundation.lazy.items

@Composable
fun SessionResultsScreen(
    finalPositions: List<FinalPosition>,
    onBack: () -> Unit
) {
    Scaffold(
        positionIndicator = {
            // Puedes agregar aquí un PositionIndicator si lo necesitas para Wear OS
        }
    ) {
        if (finalPositions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay datos de resultados",
                    style = MaterialTheme.typography.title3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                ) // Aquí termina el Text, sin coma si le sigue otro Composable

                Button( // Ahora el Button está como un elemento separado dentro del Column
                    onClick = onBack,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Volver") // Este Text está dentro de la lambda de contenido del Button
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                 contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 16.dp // Or the desired top/bottom padding
                )
            ) {
                items(finalPositions) { position ->
                    DriverPositionItem(
                        position = position,
                    )
                }
            }
        }
    }
}