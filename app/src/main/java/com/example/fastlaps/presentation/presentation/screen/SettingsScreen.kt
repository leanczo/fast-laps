package com.example.fastlaps.presentation.presentation.screen

import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.example.fastlaps.presentation.complication.DriverComplicationService
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel
import com.leandro.fastlaps.R
import kotlinx.coroutines.launch

private val leadTimeOptions = listOf(5, 10, 15, 30)

@Composable
fun SettingsScreen(
    viewModel: RaceViewModel,
    currentLang: String,
    onLanguageChange: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsPrefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    val complicationPrefs = remember { context.getSharedPreferences("driver_complication", Context.MODE_PRIVATE) }

    val driverStandings by viewModel.driverStandings.collectAsState()

    // Notification lead time
    val leadTimeIndex = remember {
        val saved = settingsPrefs.getInt("notification_lead_time", 15)
        mutableIntStateOf(leadTimeOptions.indexOf(saved).coerceAtLeast(0))
    }

    // Session toggles
    val notifyFp1 = remember { mutableStateOf(settingsPrefs.getBoolean("notify_fp1", true)) }
    val notifyFp2 = remember { mutableStateOf(settingsPrefs.getBoolean("notify_fp2", true)) }
    val notifyFp3 = remember { mutableStateOf(settingsPrefs.getBoolean("notify_fp3", true)) }
    val notifyQuali = remember { mutableStateOf(settingsPrefs.getBoolean("notify_qualifying", true)) }
    val notifySprint = remember { mutableStateOf(settingsPrefs.getBoolean("notify_sprint", true)) }
    val notifyRace = remember { mutableStateOf(settingsPrefs.getBoolean("notify_race", true)) }

    // Favorite driver
    val savedDriverId = remember { mutableStateOf(complicationPrefs.getString("driver_id", null)) }

    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.loadDriverStandings()
    }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            item {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            // Language
            item {
                val langLabel = if (currentLang == "en") "English" else "Español"
                val switchTo = if (currentLang == "en") "Español" else "English"
                SettingsRow(
                    label = stringResource(R.string.settings_language),
                    value = langLabel,
                    onClick = onLanguageChange,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Section: Notifications
            item {
                SectionHeader(stringResource(R.string.settings_notifications))
            }

            // Lead time
            item {
                val currentTime = leadTimeOptions[leadTimeIndex.intValue]
                CycleSelector(
                    label = stringResource(R.string.settings_notification_time),
                    value = stringResource(R.string.settings_minutes_before, currentTime),
                    canGoBack = leadTimeIndex.intValue > 0,
                    canGoForward = leadTimeIndex.intValue < leadTimeOptions.size - 1,
                    onPrev = {
                        leadTimeIndex.intValue--
                        settingsPrefs.edit()
                            .putInt("notification_lead_time", leadTimeOptions[leadTimeIndex.intValue])
                            .apply()
                    },
                    onNext = {
                        leadTimeIndex.intValue++
                        settingsPrefs.edit()
                            .putInt("notification_lead_time", leadTimeOptions[leadTimeIndex.intValue])
                            .apply()
                    }
                )
            }

            // Session toggles
            item {
                SessionToggle("Race", notifyRace.value) {
                    notifyRace.value = it
                    settingsPrefs.edit().putBoolean("notify_race", it).apply()
                }
            }
            item {
                SessionToggle(if (currentLang == "es") "Clasificación" else "Qualifying", notifyQuali.value) {
                    notifyQuali.value = it
                    settingsPrefs.edit().putBoolean("notify_qualifying", it).apply()
                }
            }
            item {
                SessionToggle("Sprint", notifySprint.value) {
                    notifySprint.value = it
                    settingsPrefs.edit().putBoolean("notify_sprint", it).apply()
                }
            }
            item {
                SessionToggle("FP1", notifyFp1.value) {
                    notifyFp1.value = it
                    settingsPrefs.edit().putBoolean("notify_fp1", it).apply()
                }
            }
            item {
                SessionToggle("FP2", notifyFp2.value) {
                    notifyFp2.value = it
                    settingsPrefs.edit().putBoolean("notify_fp2", it).apply()
                }
            }
            item {
                SessionToggle("FP3", notifyFp3.value) {
                    notifyFp3.value = it
                    settingsPrefs.edit().putBoolean("notify_fp3", it).apply()
                }
            }

            // Section: Favorite Driver
            item {
                SectionHeader(stringResource(R.string.settings_favorite_driver))
            }

            item {
                val currentDriver = driverStandings.find { it.Driver.driverId == savedDriverId.value }
                val currentIndex = if (currentDriver != null) driverStandings.indexOf(currentDriver) else -1
                val displayName = currentDriver?.let {
                    "${it.Driver.givenName} ${it.Driver.familyName}"
                } ?: stringResource(R.string.settings_no_driver)

                CycleSelector(
                    label = null,
                    value = displayName,
                    canGoBack = currentIndex > 0 || (currentIndex == -1 && driverStandings.isNotEmpty()),
                    canGoForward = currentIndex < driverStandings.size - 1,
                    onPrev = {
                        val newIndex = if (currentIndex <= 0) 0 else currentIndex - 1
                        if (driverStandings.isNotEmpty()) {
                            val driver = driverStandings[newIndex]
                            savedDriverId.value = driver.Driver.driverId
                            complicationPrefs.edit()
                                .putString("driver_id", driver.Driver.driverId)
                                .apply()
                            updateComplication(context)
                        }
                    },
                    onNext = {
                        val newIndex = if (currentIndex == -1) 0 else (currentIndex + 1).coerceAtMost(driverStandings.size - 1)
                        if (driverStandings.isNotEmpty()) {
                            val driver = driverStandings[newIndex]
                            savedDriverId.value = driver.Driver.driverId
                            complicationPrefs.edit()
                                .putString("driver_id", driver.Driver.driverId)
                                .apply()
                            updateComplication(context)
                        }
                    }
                )
            }

            // Bottom spacer
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

private fun updateComplication(context: Context) {
    try {
        val updater = ComplicationDataSourceUpdateRequester.create(
            context,
            ComponentName(context, DriverComplicationService::class.java)
        )
        updater.requestUpdateAll()
    } catch (_: Exception) { }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption1,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    icon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun CycleSelector(
    label: String?,
    value: String,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.caption2,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "◀",
                style = MaterialTheme.typography.body2,
                color = if (canGoBack) MaterialTheme.colors.secondary else Color.DarkGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(enabled = canGoBack, onClick = onPrev)
                    .padding(6.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.caption1,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )
            Text(
                text = "▶",
                style = MaterialTheme.typography.body2,
                color = if (canGoForward) MaterialTheme.colors.secondary else Color.DarkGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(enabled = canGoForward, onClick = onNext)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
private fun SessionToggle(
    label: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(shape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), shape)
            .clickable { onToggle(!enabled) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    if (enabled) Color(0xFF4CAF50) else Color(0xFF555555),
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f)
        )
    }
}
