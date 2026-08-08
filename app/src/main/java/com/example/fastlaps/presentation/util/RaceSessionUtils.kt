package com.example.fastlaps.presentation.util

import Race
import SessionTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

private fun SessionTime.toOffsetDateTimeOrNull(): OffsetDateTime? {
    if (date.isEmpty() || time.isEmpty()) return null
    return try {
        LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time.trimEnd('Z')))
            .atOffset(ZoneOffset.UTC)
    } catch (_: Exception) {
        null
    }
}

fun Race.earliestSessionStart(): OffsetDateTime? {
    val sessions = listOfNotNull(
        FirstPractice,
        SecondPractice,
        ThirdPractice,
        Sprint,
        Qualifying,
        SessionTime(date, time)
    )
    return sessions.mapNotNull { it.toOffsetDateTimeOrNull() }.minOrNull()
}

fun Race.hasWeekendStarted(now: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)): Boolean {
    return earliestSessionStart()?.isBefore(now) ?: false
}
