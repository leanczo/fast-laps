package com.example.fastlaps.presentation.presentation.component

import NewsModel
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
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
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(item.url.toUri())
                RemoteActivityHelper(context).startRemoteActivity(intent)
            }
            .padding(12.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.body2.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = item.description,
            style = MaterialTheme.typography.caption2.copy(fontSize = 11.sp),
            color = Color.White.copy(alpha = 0.6f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = Color.White.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.caption2.copy(fontSize = 10.sp),
                    color = Color.White.copy(alpha = 0.4f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PhoneAndroid,
                    contentDescription = stringResource(R.string.open_phone),
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = stringResource(R.string.open_phone),
                    style = MaterialTheme.typography.caption2.copy(fontSize = 10.sp),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

private fun formatNewsDate(pubDateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
        val date = inputFormat.parse(pubDateString) ?: return pubDateString
        val outputFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        outputFormat.format(date)
    } catch (e: Exception) {
        pubDateString
    }
}
