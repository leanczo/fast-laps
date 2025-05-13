import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
            // You might want to center this "Loading..." text.
            // See previous explanation on how to center content in a Box.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // Centering the loading text
            ) {
                Text("Loading...", color = MaterialTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Título "Circuits"
                Text(
                    text = "Circuits",
                    style = MaterialTheme.typography.title3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                ScalingLazyColumn {
                    sessions.entries.reversed().forEach { (meetingKey, sessionList) ->
                        item(key = meetingKey) {
                            val circuitName = sessionList.first().circuit_short_name
                            val countryName = sessionList.first().country_name
                            Button(
                                onClick = { viewModel.toggleMeetingSessions(meetingKey) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.surface,
                                    contentColor = Color(0xFFFFFFFF), // Consider using onSurface from your theme
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "$circuitName, $countryName",
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }

                        if (expandedMeetingKey == meetingKey) {
                            items(
                                items = sessionList,
                                key = { it.session_key }
                            ) { session ->
                                Button(
                                    onClick = { onSessionClick(session.session_key) },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.secondary,
                                        contentColor = MaterialTheme.colors.onSecondary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = session.session_name,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                        }
                    }

                    // Add the footer as a new item block here
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(16.dp)) // Add some space above the footer content
                            Text(
                                text = "v1.4", // Cambia esto por tu versión actual
                                style = MaterialTheme.typography.caption3,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "© 2025 Leandro Cardozo", // Reemplaza con tu nombre
                                style = MaterialTheme.typography.caption3,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp)) // Add some space below the footer if needed
                        }
                    }
                }
            }
        }
    }
}