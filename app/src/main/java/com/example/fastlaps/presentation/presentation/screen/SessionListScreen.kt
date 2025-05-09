import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.model.Session
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SessionListScreen(
    viewModel: RaceViewModel,
    onSessionClick: (Int) -> Unit
) {
    val sessions: Map<Int, List<Session>> by viewModel.sessions.collectAsState()
    val expandedMeetingKey: Int? by viewModel.expandedMeetingKey.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.TopCenter
    ) {
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), // Make this Box fill the parent Box
                contentAlignment = Alignment.Center // Center its content
            ) {
                Text("Cargando...", color = MaterialTheme.colors.primary)
            }
        } else {
            ScalingLazyColumn {
                // Iterate directly over the map entries to define items
                sessions.entries.forEach { (meetingKey, sessionList) ->
                    // Define an item for the meeting button
                    item(key = meetingKey) { // Provide a stable key for the meeting item
                        val circuitName = sessionList.first().circuit_short_name
                        val countryName = sessionList.first().country_name
                        Button(
                            onClick = { viewModel.toggleMeetingSessions(meetingKey) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$circuitName, $countryName")
                        }
                    }

                    // If the meeting is expanded, define items for the sessions
                    if (expandedMeetingKey == meetingKey) {
                        items(
                            items = sessionList, // The list of sessions
                            key = { it.session_key } // Provide a stable key for each session
                        ) { session -> // The individual session object
                            Button(
                                onClick = { onSessionClick(session.session_key) },
                                  colors = ButtonDefaults.buttonColors(
                                      backgroundColor = MaterialTheme.colors.secondary
                                    ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                            ) {
                                Text(session.session_name)
                            }
                        }
                    }
                }
            }
        }
    }
}