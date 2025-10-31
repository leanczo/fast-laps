
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun ConstructorStandingItem(
    standing: ConstructorStanding,
    modifier: Modifier = Modifier
) {
    val positionColor = when (standing.position.toIntOrNull() ?: 0) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colors.primary
    }

    fun getCountryFlagEmoji(nationality: String): String {
        return when (nationality.lowercase()) {
            "british" -> "🇬🇧"
            "spanish" -> "🇪🇸"
            "dutch" -> "🇳🇱"
            "monegasque" -> "🇲🇨"
            "australian" -> "🇦🇺"
            "thai" -> "🇹🇭"
            "french" -> "🇫🇷"
            "canadian" -> "🇨🇦"
            "japanese" -> "🇯🇵"
            "chinese" -> "🇨🇳"
            "german" -> "🇩🇪"
            "italian" -> "🇮🇹"
            "american" -> "🇺🇸"
            "swiss" -> "🇨🇭"
            "new zealander" -> "🇳🇿"
            "argentine" -> "🇦🇷"
            "brazilian" -> "🇧🇷"
            "austrian" -> "🇦🇹"
            else -> "🏳️"
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colors.onSurface,
        onClick = {  }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#${standing.position}",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = positionColor,
            )

            Column(
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text(
                    text = standing.Constructor.name,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = getCountryFlagEmoji(standing.Constructor.nationality) + " " + standing.Constructor.nationality,
                        style = MaterialTheme.typography.caption2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Text(
                text = standing.points,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}