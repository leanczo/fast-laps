package com.example.fastlaps.presentation.model

data class FinalPosition(
    val driverNumber: Int,
    val position: Int,
    val timestamp: String,
    var driverInfo: Driver? = null
)