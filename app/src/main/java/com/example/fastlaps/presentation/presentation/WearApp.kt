package com.example.fastlaps.presentation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.Session
import com.example.fastlaps.presentation.theme.FastlapsTheme

@Composable
private fun SessionItem(session: Session) {
    Button(
        onClick = { /* Acción para la sesión */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary
        )
    ) {
        Text(
            "${session.session_name} (${session.session_type})",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WearApp() {
    val viewModel = remember { RaceViewModel() }
    val sessions by viewModel.sessions.collectAsState()
    val expandedMeetingKey by viewModel.expandedMeetingKey.collectAsState()

    FastlapsTheme {
        SessionListScreen(
            sessions = sessions,
            expandedMeetingKey = expandedMeetingKey,
            onMeetingClick = { meetingKey -> viewModel.toggleMeetingSessions(meetingKey) }
        )
    }
}

@Composable
private fun SessionListScreen(
    sessions: Map<Int, List<Session>>,
    expandedMeetingKey: Int?,
    onMeetingClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.TopCenter
    ) {
        if (sessions.isEmpty()) {
            Text("Cargando...", color = MaterialTheme.colors.primary)
        } else {
            ScalingLazyColumn {
                sessions.forEach { (meetingKey, sessionList) ->
                    val circuitName = sessionList.first().circuit_short_name
                    val countryName = sessionList.first().country_name

                    // Botón de la carrera principal
                    item {
                        Button(
                            onClick = { onMeetingClick(meetingKey) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$circuitName, $countryName", textAlign = TextAlign.Center)
                        }
                    }

                    // Sesiones expandidas
                    if (expandedMeetingKey == meetingKey) {
                        sessionList.forEach { session ->
                            item {
                                SessionItem(session = session)
                            }
                        }
                    }
                }
            }
        }
    }
}

