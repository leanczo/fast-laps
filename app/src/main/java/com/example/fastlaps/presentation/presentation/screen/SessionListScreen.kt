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
import androidx.compose.ui.res.stringResource
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
import com.leandro.fastlaps.R


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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = MaterialTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Circuits",
                    style = MaterialTheme.typography.body2,
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

                    // Footer
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(id = R.string.app_version),
                                style = MaterialTheme.typography.caption3,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = stringResource(id = R.string.copyright),
                                style = MaterialTheme.typography.caption3,
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}