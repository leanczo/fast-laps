package com.example.fastlaps.presentation.model

data class PositionData(
    val session_key: Int,
    val meeting_key: Int,
    val driver_number: Int,
    val date: String,
    val position: Int
)