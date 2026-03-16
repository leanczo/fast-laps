import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R
import java.time.LocalDate

private fun countryFlag(country: String): String {
    return when (country) {
        "Australia" -> "\uD83C\uDDE6\uD83C\uDDFA"
        "China" -> "\uD83C\uDDE8\uD83C\uDDF3"
        "Japan" -> "\uD83C\uDDEF\uD83C\uDDF5"
        "Bahrain" -> "\uD83C\uDDE7\uD83C\uDDED"
        "Saudi Arabia" -> "\uD83C\uDDF8\uD83C\uDDE6"
        "USA" -> "\uD83C\uDDFA\uD83C\uDDF8"
        "Canada" -> "\uD83C\uDDE8\uD83C\uDDE6"
        "Monaco" -> "\uD83C\uDDF2\uD83C\uDDE8"
        "Spain" -> "\uD83C\uDDEA\uD83C\uDDF8"
        "Austria" -> "\uD83C\uDDE6\uD83C\uDDF9"
        "UK" -> "\uD83C\uDDEC\uD83C\uDDE7"
        "Belgium" -> "\uD83C\uDDE7\uD83C\uDDEA"
        "Hungary" -> "\uD83C\uDDED\uD83C\uDDFA"
        "Netherlands" -> "\uD83C\uDDF3\uD83C\uDDF1"
        "Italy" -> "\uD83C\uDDEE\uD83C\uDDF9"
        "Azerbaijan" -> "\uD83C\uDDE6\uD83C\uDDFF"
        "Singapore" -> "\uD83C\uDDF8\uD83C\uDDEC"
        "Mexico" -> "\uD83C\uDDF2\uD83C\uDDFD"
        "Brazil" -> "\uD83C\uDDE7\uD83C\uDDF7"
        "Qatar" -> "\uD83C\uDDF6\uD83C\uDDE6"
        "UAE" -> "\uD83C\uDDE6\uD83C\uDDEA"
        "Portugal" -> "\uD83C\uDDF5\uD83C\uDDF9"
        "France" -> "\uD83C\uDDEB\uD83C\uDDF7"
        "Germany" -> "\uD83C\uDDE9\uD83C\uDDEA"
        else -> "\uD83C\uDFC1"
    }
}

@Composable
fun SessionListScreen(
    viewModel: RaceViewModel,
    onRaceClick: (round: Int, raceName: String) -> Unit,
    onBack: () -> Unit,
) {
    val races by viewModel.races.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val today = LocalDate.now().toString()
    val pastRaces = races.filter { it.date <= today }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                LoadingIndicator()
            }

            errorState != null -> {
                ErrorMessage(
                    errorState = errorState,
                    onRetry = { viewModel.loadRaces() }
                )
            }

            pastRaces.isEmpty() -> {
                EmptyState(onRetry = { viewModel.loadRaces() })
            }

            else -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.races),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    ScalingLazyColumn {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = { viewModel.loadRaces() },
                                    isErrorState = false
                                )
                            }
                        }

                        items(pastRaces.reversed()) { race ->
                            val flag = countryFlag(race.Circuit.Location.country)
                            val name = race.raceName.replace(" Grand Prix", "")
                            Button(
                                onClick = { onRaceClick(race.round.toInt(), race.raceName) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.surface,
                                    contentColor = Color.White
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "$flag $name\n${race.Circuit.Location.locality}",
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
