import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
private fun ErrorRetrySection(errorMessage: String?, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = errorMessage ?: "Error",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption2,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.size(24.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.error,
                    contentColor = MaterialTheme.colors.onError
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Retry", style = MaterialTheme.typography.caption2)
            }
        }
    }
}