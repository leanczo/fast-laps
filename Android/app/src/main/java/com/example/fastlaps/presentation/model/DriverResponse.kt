package com.example.fastlaps.presentation.model

data class Driver(
    val session_key: Int,
    val driver_number: Int,
    val broadcast_name: String,
    val full_name: String,
    val team_name: String,
    val team_colour: String
)
