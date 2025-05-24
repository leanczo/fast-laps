import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import androidx.compose.ui.unit.dp
import com.example.fastlaps.presentation.presentation.component.DriverStandingItem
import androidx.compose.runtime.getValue

@Composable
fun PilotsScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val driverStandings by viewModel.driverStandings.collectAsState()
    val isLoading by viewModel.isLoadingDrivers.collectAsState()
    val errorState by viewModel.driverErrorState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDriverStandings()
    }

    Box(
        modifier = modifier
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
                    onRetry = { viewModel.loadDriverStandings() }
                )
            }

            driverStandings.isEmpty() -> {
                EmptyState(onRetry = { viewModel.loadDriverStandings() })
            }

            else -> {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Drivers",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = { viewModel.loadDriverStandings() },
                                    isErrorState = false
                                )
                            }
                        }

                        items(driverStandings.size) { index ->
                            val driver = driverStandings[index]
                            DriverStandingItem(
                                driver = driver,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
