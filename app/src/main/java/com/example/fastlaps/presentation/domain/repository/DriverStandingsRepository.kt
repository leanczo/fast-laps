package com.example.fastlaps.presentation.repository

import ConstructorStandingsResponse
import DriverStanding
import DriverStandingsResponse
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
}