package com.example.fastlaps.presentation.complication

import DriverStanding
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import android.content.ComponentName
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import com.example.fastlaps.presentation.util.F1Constants
import kotlinx.coroutines.launch
import java.util.Calendar

class DriverComplicationConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        // Set canceled as default - only OK if user selects a driver
        setResult(Activity.RESULT_CANCELED)

        val complicationId = intent?.getIntExtra(
            "android.support.wearable.complications.EXTRA_CONFIG_COMPLICATION_ID",
            intent?.getIntExtra(
                "androidx.watchface.complications.datasource.EXTRA_CONFIG_COMPLICATION_ID", -1
            ) ?: -1
        ) ?: -1

        setContent {
            DriverPickerScreen(
                complicationId = complicationId,
                onDriverSelected = { driverId, driverCode, driverName ->
                    val prefs = getSharedPreferences("driver_complication", MODE_PRIVATE)
                    prefs.edit()
                        .putString("driver_id_$complicationId", driverId)
                        .putString("driver_code_$complicationId", driverCode)
                        .putString("driver_name_$complicationId", driverName)
                        .apply()

                    // Request complication update
                    val updateRequester = ComplicationDataSourceUpdateRequester.create(
                        this,
                        ComponentName(this, DriverComplicationService::class.java)
                    )
                    updateRequester.requestUpdateAll()

                    setResult(Activity.RESULT_OK)
                    finish()
                }
            )
        }
    }
}

@Composable
private fun DriverPickerScreen(
    complicationId: Int,
    onDriverSelected: (driverId: String, driverCode: String, driverName: String) -> Unit
) {
    val drivers = remember { mutableStateOf<List<DriverStanding>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        coroutineScope.launch {
            try {
                val repo = DriverStandingsRepository()
                val year = Calendar.getInstance().get(Calendar.YEAR)
                drivers.value = repo.getProcessedDriverStandings(year)
            } catch (_: Exception) {
                drivers.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading.value) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.body2,
                color = Color.White
            )
        } else {
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
                    Text(
                        text = "Select Driver",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                items(drivers.value) { driver ->
                    val teamColor = F1Constants.teamColor(driver.Constructors.firstOrNull()?.constructorId)
                    val flag = F1Constants.nationalityFlag(driver.Driver.nationality)
                    val code = driver.Driver.code.ifEmpty { driver.Driver.familyName.take(3).uppercase() }
                    val fullName = "${driver.Driver.givenName} ${driver.Driver.familyName}"
                    val shape = RoundedCornerShape(10.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .height(IntrinsicSize.Min)
                            .clip(shape)
                            .background(Color(0xFF1A1A1A))
                            .border(1.dp, Color(0xFF333333), shape)
                            .clickable { onDriverSelected(driver.Driver.driverId, code, fullName) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(teamColor)
                        )
                        Text(
                            text = "$flag $fullName",
                            style = MaterialTheme.typography.body2,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                        Text(
                            text = "P${driver.position}",
                            style = MaterialTheme.typography.caption2,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }
        }
    }
}
