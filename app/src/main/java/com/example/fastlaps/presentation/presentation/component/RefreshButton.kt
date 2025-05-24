import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme

@Composable
fun RefreshButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    isErrorState: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier.size(45.dp), // Tamaño más razonable para wearables
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isErrorState) MaterialTheme.colors.error
            else MaterialTheme.colors.secondary,
            contentColor = if (isErrorState) MaterialTheme.colors.onError
            else MaterialTheme.colors.onSecondary
        ),
        shape = MaterialTheme.shapes.small,
        enabled = !isLoading,
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh",
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    rotationZ = if (isLoading) rotation.value else 0f
                }
        )
    }
}