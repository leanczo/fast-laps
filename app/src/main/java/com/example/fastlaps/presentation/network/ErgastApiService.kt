package com.example.fastlaps.presentation.network

import ConstructorStandingsResponse
import DriverStandingsResponse
import RaceTableResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ErgastApiService {
    @GET("f1/{year}/driverStandings.json")
    suspend fun getDriverStandings(
        @Path("year") year: Int
    ): DriverStandingsResponse

    @GET("f1/{year}/constructorstandings.json")
    suspend fun getConstructorStandings(@Path("year") year: Int): ConstructorStandingsResponse

    @GET("f1/{year}.json")
    suspend fun getRaceSchedule(@Path("year") year: Int): RaceTableResponse

    @GET("f1/{year}/{round}/results.json")
    suspend fun getRaceResults(
        @Path("year") year: Int,
        @Path("round") round: Int
    ): RaceTableResponse

    @GET("f1/{year}/{round}/qualifying.json")
    suspend fun getQualifyingResults(
        @Path("year") year: Int,
        @Path("round") round: Int
    ): RaceTableResponse

    @GET("f1/{year}/{round}/sprint.json")
    suspend fun getSprintResults(
        @Path("year") year: Int,
        @Path("round") round: Int
    ): RaceTableResponse
}
