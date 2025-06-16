import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R

@Composable
fun ConstructorsScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val standings by viewModel.constructorStandings.collectAsState()
    val isLoading by viewModel.isLoadingConstructors.collectAsState()
    val errorState by viewModel.constructorErrorState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConstructorStandings()
    }

    Scaffold(
        modifier = modifier,
        positionIndicator = { PositionIndicator(scrollState = rememberScrollState()) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    LoadingIndicator()
                }

                errorState != null -> {
                    ErrorMessage(
                        errorState = errorState,
                        onRetry = { viewModel.loadConstructorStandings() }
                    )
                }

                standings.isEmpty() -> {
                    EmptyState(onRetry = { viewModel.loadConstructorStandings() })
                }

                else -> {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.teams),
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
                                        onClick = { viewModel.loadConstructorStandings() },
                                        isErrorState = false
                                    )
                                }
                            }

                            items(standings.size) { index ->
                                val standing = standings[index]
                                ConstructorStandingItem(
                                    standing,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}