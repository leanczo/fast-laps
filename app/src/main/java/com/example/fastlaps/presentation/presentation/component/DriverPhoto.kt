package com.example.fastlaps.presentation.presentation.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.fastlaps.presentation.util.F1Constants

@Composable
fun DriverPhoto(
    driverId: String,
    fallbackText: String,
    teamColor: Color,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val imageUrl = remember(driverId) { F1Constants.driverImageUrl(driverId) }

    if (imageUrl.isEmpty()) {
        FallbackAvatar(fallbackText, teamColor, size, modifier)
        return
    }

    val sizePx = (size.value * 2).toInt()

    Box(modifier = modifier) {
        FallbackAvatar(fallbackText, teamColor, size)

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .size(Size(sizePx, sizePx))
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(200)
                .listener(
                    onError = { _, result ->
                        Log.d("DriverPhoto", "Failed to load: $driverId - ${result.throwable.message}")
                    }
                )
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, teamColor, CircleShape)
        )
    }
}

@Composable
private fun FallbackAvatar(text: String, color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(Color(0xFF2A2A2A), CircleShape)
            .border(2.dp, color, CircleShape)
    ) {
        Text(
            text = text.take(3).uppercase(),
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
