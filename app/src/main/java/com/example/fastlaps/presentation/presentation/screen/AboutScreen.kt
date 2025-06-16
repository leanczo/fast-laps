import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.leandro.fastlaps.R

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        positionIndicator = {
            PositionIndicator(scrollState = scrollState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.about_data_provided_by),
                style = MaterialTheme.typography.caption2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            )

            // OpenF1 section
            Text(
                text = stringResource(R.string.about_openf1),
                style = MaterialTheme.typography.caption3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            // Jolpica F1 section
            Text(
                text = stringResource(R.string.about_jolpica),
                style = MaterialTheme.typography.caption3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.about_jolpica_description),
                style = MaterialTheme.typography.caption3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // General disclaimer
            Text(
                text = stringResource(R.string.about_disclaimer),
                style = MaterialTheme.typography.caption3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Usage policy
            Text(
                text = stringResource(R.string.about_usage_policy),
                style = MaterialTheme.typography.caption3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Official disclaimer
            Text(
                text = stringResource(R.string.about_official_disclaimer),
                style = MaterialTheme.typography.caption3.copy(
                    color = MaterialTheme.colors.error
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}