import com.example.fastlaps.presentation.presentation.component.NewsCard
import androidx.compose.foundation.background
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
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

@Composable
fun NewsScreen(
    viewModel: RaceViewModel,
    currentLang: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val news by viewModel.news.collectAsState()
    val isLoading by viewModel.isLoadingNews.collectAsState()
    val errorState by viewModel.newsErrorState.collectAsState()

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(currentLang) {
        viewModel.loadNews(currentLang)
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
                    onRetry = { viewModel.retryLoadingNews(currentLang) }
                )
            }

            news.isEmpty() -> {
                EmptyState(onRetry = { viewModel.retryLoadingNews(currentLang) })
            }

            else -> {
                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.news_title),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

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
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                RefreshButton(
                                    isLoading = isLoading,
                                    onClick = { viewModel.retryLoadingNews(currentLang) },
                                    isErrorState = errorState != null
                                )
                            }
                        }

                        items(news) { item ->
                            NewsCard(
                                item = item,
                                modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
