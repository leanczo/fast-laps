package com.example.fastlaps.presentation.presentation.screen

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastlaps.presentation.presentation.component.DriverPhoto
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import LoadingIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.example.fastlaps.presentation.complication.DriverComplicationService
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.example.fastlaps.presentation.util.F1Constants
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

@Composable
fun DriverDetailScreen(
    viewModel: RaceViewModel,
    driverId: String,
    onBack: () -> Unit
) {
    val driver by viewModel.selectedDriver.collectAsState()
    val seasonResults by viewModel.driverSeasonResults.collectAsState()
    val isLoading by viewModel.isLoadingDriverDetail.collectAsState()

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(driverId) {
        viewModel.loadDriverDetail(driverId)
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val lang = LocalConfiguration.current.locales[0].language

    Scaffold {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent { event ->
                    coroutineScope.launch { listState.scrollBy(event.verticalScrollPixels) }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            driver?.let { standing ->
                val flag = F1Constants.nationalityFlag(standing.Driver.nationality)
                val teamColor = F1Constants.teamColor(standing.Constructors.firstOrNull()?.constructorId)
                val teamName = standing.Constructors.firstOrNull()?.name ?: ""
                val number = standing.Driver.permanentNumber

                // Driver number + flag
                item {
                    Text(
                        text = "$flag  #$number",
                        style = MaterialTheme.typography.title3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                // Full name
                item {
                    Text(
                        text = "${standing.Driver.givenName}\n${standing.Driver.familyName}",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                }

                // Team
                item {
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.body2,
                        color = teamColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                }

                // Stats row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge(
                            value = "P${standing.position}",
                            label = if (lang == "es") "Pos" else "Pos"
                        )
                        StatBadge(
                            value = standing.points,
                            label = "Pts"
                        )
                        StatBadge(
                            value = standing.wins,
                            label = if (lang == "es") "Vic" else "Wins"
                        )
                    }
                }

                // Set as complication button
                item {
                    val context = LocalContext.current
                    val confirmed = remember { mutableStateOf(false) }
                    val shape = RoundedCornerShape(10.dp)

                    val bgColor = if (confirmed.value) Color(0xFF1B5E20).copy(alpha = 0.3f)
                    else MaterialTheme.colors.secondary.copy(alpha = 0.15f)
                    val borderColor = if (confirmed.value) Color(0xFF4CAF50).copy(alpha = 0.5f)
                    else MaterialTheme.colors.secondary.copy(alpha = 0.4f)
                    val contentColor = if (confirmed.value) Color(0xFF4CAF50)
                    else MaterialTheme.colors.secondary

                    LaunchedEffect(confirmed.value) {
                        if (confirmed.value) {
                            kotlinx.coroutines.delay(1500)
                            confirmed.value = false
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clip(shape)
                            .background(bgColor)
                            .border(1.dp, borderColor, shape)
                            .clickable {
                                if (!confirmed.value) {
                                    val prefs = context.getSharedPreferences("driver_complication", 0)
                                    prefs.edit()
                                        .putString("driver_id", standing.Driver.driverId)
                                        .apply()
                                    val updater = ComplicationDataSourceUpdateRequester.create(
                                        context,
                                        ComponentName(context, DriverComplicationService::class.java)
                                    )
                                    updater.requestUpdateAll()
                                    confirmed.value = true
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (confirmed.value) Icons.Default.Check else Icons.Default.Watch,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (confirmed.value) stringResource(R.string.complication_set_done)
                            else stringResource(R.string.set_complication),
                            style = MaterialTheme.typography.caption2,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }

                // Season results header
                item {
                    Text(
                        text = stringResource(R.string.season_results),
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                if (isLoading) {
                    item {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(seasonResults) { race ->
                        val result = race.Results.firstOrNull()
                        if (result != null) {
                            DriverRaceResultItem(
                                raceName = race.raceName.replace(" Grand Prix", ""),
                                country = race.Circuit.Location.country,
                                position = result.position,
                                status = result.status,
                                points = result.points,
                                teamColor = teamColor,
                                lang = lang,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(value: String, label: String) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun DriverRaceResultItem(
    raceName: String,
    country: String,
    position: String,
    status: String,
    points: String,
    teamColor: Color,
    lang: String,
    modifier: Modifier = Modifier
) {
    val positionColor = when (position) {
        "1" -> Color(0xFFFFD700)
        "2" -> Color(0xFFC0C0C0)
        "3" -> Color(0xFFCD7F32)
        else -> Color.White
    }

    val flag = F1Constants.countryFlag(country)
    val isFinished = status == "Finished"
    val isDNF = !isFinished && !status.startsWith("+") && status != "Lapped"

    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team color bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(teamColor)
        )

        // Position
        Text(
            text = position,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = positionColor,
            modifier = Modifier.padding(start = 10.dp, end = 6.dp)
        )

        // Race info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "$flag $raceName",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (isDNF) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.caption2,
                    color = Color(0xFFEF5350)
                )
            }
        }

        // Points earned
        if (points.toDoubleOrNull()?.let { it > 0 } == true) {
            Text(
                text = "+$points",
                style = MaterialTheme.typography.caption2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}
