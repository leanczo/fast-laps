package com.example.fastlaps.presentation.repository

import ConstructorStandingsResponse
import DriverStanding
import DriverStandingsResponse
import Lap
import PitStop
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
        return response.MRData.StandingsTable.StandingsLists.firstOrNull()?.DriverStandings ?: emptyList()
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

    suspend fun getDriverSeasonResults(year: Int, driverId: String): List<Race> {
        val response = api.getDriverSeasonResults(year, driverId)
        return response.MRData.RaceTable.Races
    }

    suspend fun getSingleLap(year: Int, round: Int, lap: Int): Lap? {
        return try {
            val response = api.getSingleLap(year, round, lap)
            response.MRData.RaceTable.Races.firstOrNull()?.Laps?.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getAllSeasonResults(year: Int): List<Race> {
        val pageSize = 100
        var offset = 0
        val raceMap = mutableMapOf<String, Race>()

        while (true) {
            val response = api.getAllSeasonResults(year, pageSize, offset)
            val total = response.MRData.total.toIntOrNull() ?: 0

            response.MRData.RaceTable.Races.forEach { race ->
                val existing = raceMap[race.round]
                if (existing != null) {
                    raceMap[race.round] = existing.copy(
                        Results = existing.Results + race.Results
                    )
                } else {
                    raceMap[race.round] = race
                }
            }

            offset += pageSize
            if (offset >= total || response.MRData.RaceTable.Races.isEmpty()) break
        }

        return raceMap.values.sortedBy { it.round.toIntOrNull() ?: 0 }
    }

    suspend fun getPitStops(year: Int, round: Int): List<PitStop> {
        val response = api.getPitStops(year, round)
        return response.MRData.RaceTable.Races.firstOrNull()?.PitStops ?: emptyList()
    }

    suspend fun getConstructorSeasonResults(year: Int, constructorId: String): List<Race> {
        val response = api.getConstructorSeasonResults(year, constructorId)
        return response.MRData.RaceTable.Races
    }
}
