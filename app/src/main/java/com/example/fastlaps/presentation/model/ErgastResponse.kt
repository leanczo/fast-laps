package com.example.fastlaps.presentation.model

data class ErgastResponse(
    val MRData: MRData
)

data class MRData(
    val RaceTable: RaceTable
)

data class RaceTable(
    val Races: List<Race>
)

data class Race(
    val raceName: String,
    val Results: List<Result>
)

data class Result(
    val position: String,
    val Driver: Driver,
    val Constructor: Constructor
)

data class Driver(
    val givenName: String,
    val familyName: String
)

data class Constructor(
    val name: String
)
