package com.example.fastlaps.presentation.model

data class Session(
    val session_key: Int,
    val session_name: String,
    val date_start: String,
    val date_end: String,
    val gmt_offset: String,
    val session_type: String,
    val meeting_key: Int,
    val location: String,
    val country_name: String,
    val circuit_short_name: String
)

