package com.example.fastlaps.presentation.repository

import ConstructorStandingsResponse
import DriverStanding
import DriverStandingsResponse
import QualifyingResult
import Race
import RaceResult
import com.example.fastlaps.presentation.network.ErgastRetrofitInstance

class DriverStandingsRepository {
    private val api = ErgastRetrofitInstance.api

    suspend fun getDriverStandings(year: Int): DriverStandingsResponse {
        return api.getDriverStandings(year)
    }

    suspend fun getProcessedDriverStandings(year: Int): List<DriverStanding> {
        val response = getDriverStandings(year)
        return response.MRData.StandingsTable.StandingsLists.first().DriverStandings
    }

    suspend fun getTopDrivers(year: Int, count: Int): List<DriverStanding> {
        val standings = getProcessedDriverStandings(year)
        return standings.take(count)
    }

    suspend fun getConstructorStandings(year: Int): ConstructorStandingsResponse {
        return api.getConstructorStandings(year)
    }

    suspend fun getRaceSchedule(year: Int): List<Race> {
        val response = api.getRaceSchedule(year)
        return response.MRData.RaceTable.Races
    }

    suspend fun getRaceResults(year: Int, round: Int): List<RaceResult> {
        val response = api.getRaceResults(year, round)
        return response.MRData.RaceTable.Races.firstOrNull()?.Results ?: emptyList()
    }

    suspend fun getQualifyingResults(year: Int, round: Int): List<QualifyingResult> {
        val response = api.getQualifyingResults(year, round)
        return response.MRData.RaceTable.Races.firstOrNull()?.QualifyingResults ?: emptyList()
    }

    suspend fun getSprintResults(year: Int, round: Int): List<RaceResult> {
        val response = api.getSprintResults(year, round)
        return response.MRData.RaceTable.Races.firstOrNull()?.SprintResults ?: emptyList()
    }
}
