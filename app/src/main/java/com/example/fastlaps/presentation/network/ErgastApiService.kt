package com.example.fastlaps.presentation.network

import DriverStandingsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ErgastApiService {
    @GET("f1/{year}/driverStandings.json")
    suspend fun getDriverStandings(
        @Path("year") year: Int
    ): DriverStandingsResponse
}