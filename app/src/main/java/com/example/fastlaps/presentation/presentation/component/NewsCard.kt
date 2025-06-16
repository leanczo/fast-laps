
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.leandro.fastlaps.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun NewsCard(item: NewsModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val formattedDate = formatNewsDate(item.date)

    Card(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(item.url.toUri())

            RemoteActivityHelper(context).startRemoteActivity(intent)
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.title3.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Text(
                text = item.description,
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.caption2.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhoneAndroid,
                    contentDescription = stringResource(R.string.open_phone),
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.open_phone),
                    style = MaterialTheme.typography.caption2.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colors.primary
                    )
                )
            }
        }
    }
}

private fun formatNewsDate(pubDateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
        val date = inputFormat.parse(pubDateString) ?: return pubDateString
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        outputFormat.format(date)
    } catch (e: Exception) {
        pubDateString
    }
}