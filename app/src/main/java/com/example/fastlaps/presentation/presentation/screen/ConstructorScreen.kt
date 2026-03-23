import com.example.fastlaps.presentation.presentation.component.ConstructorStandingItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.component.YearSelector
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

@Composable
fun ConstructorsScreen(
    viewModel: RaceViewModel,
    onConstructorClick: (constructorId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val standings by viewModel.constructorStandings.collectAsState()
    val isLoading by viewModel.isLoadingConstructors.collectAsState()
    val errorState by viewModel.constructorErrorState.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(selectedYear) {
        viewModel.loadConstructorStandings()
    }

    Scaffold(
        modifier = modifier,
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
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
                        onRetry = { viewModel.loadConstructorStandings(forceRefresh = true) }
                    )
                }

                standings.isEmpty() -> {
                    EmptyState(onRetry = { viewModel.loadConstructorStandings(forceRefresh = true) })
                }

                else -> {
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }

                    ScalingLazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .onRotaryScrollEvent { event ->
                                coroutineScope.launch { listState.scrollBy(event.verticalScrollPixels) }
                                true
                            }
                            .focusRequester(focusRequester)
                            .focusable()
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.teams),
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        item {
                            YearSelector(
                                selectedYear = selectedYear,
                                currentYear = viewModel.currentYear,
                                onYearChange = { viewModel.setSelectedYear(it) }
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = { viewModel.loadConstructorStandings(forceRefresh = true) },
                                    isErrorState = false
                                )
                            }
                        }

                        items(standings.size) { index ->
                            val standing = standings[index]
                            ConstructorStandingItem(
                                standing = standing,
                                onClick = { onConstructorClick(standing.Constructor.constructorId) },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
