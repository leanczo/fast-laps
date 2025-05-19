import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel

@Composable
fun SessionListScreen(
    viewModel: RaceViewModel,
    onSessionClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val sessions by viewModel.sessions.collectAsState()
    val expandedMeetingKey by viewModel.expandedMeetingKey.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (errorState != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorState ?: "Connection error",
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption2,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        RefreshButton(
                            isLoading = isLoading,
                            onClick = { viewModel.loadSessions() },
                            isErrorState = true
                        )
                    }
                }
            }

            if (sessions.isEmpty() && errorState == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        RefreshButton(
                            isLoading = true,
                            onClick = {},
                            isErrorState = false
                        )
                    } else {
                        Text("Loading...", color = MaterialTheme.colors.primary)
                    }
                }
            } else if (sessions.isNotEmpty()) {
                Text(
                    text = "Circuits",
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                ScalingLazyColumn {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            RefreshButton(
                                isLoading = isLoading,
                                onClick = { viewModel.loadSessions() },
                                isErrorState = false
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    sessions.entries.reversed().forEach { (meetingKey, sessionList) ->
                        item(key = meetingKey) {
                            val circuitName = sessionList.first().circuit_short_name
                            val countryName = sessionList.first().country_name
                            Button(
                                onClick = { viewModel.toggleMeetingSessions(meetingKey) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.surface,
                                    contentColor = Color(0xFFFFFFFF),
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
                }
            }
        }
    }
}
