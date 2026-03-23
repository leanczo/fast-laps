package com.example.fastlaps.presentation.network

import ConstructorStandingsResponse
import DriverStandingsResponse
import RaceTableResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("f1/{year}/drivers/{driverId}/results.json")
    suspend fun getDriverSeasonResults(
        @Path("year") year: Int,
        @Path("driverId") driverId: String
    ): RaceTableResponse

    @GET("f1/{year}/results.json")
    suspend fun getAllSeasonResults(
        @Path("year") year: Int,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): RaceTableResponse

    @GET("f1/{year}/{round}/laps/{lap}.json")
    suspend fun getSingleLap(
        @Path("year") year: Int,
        @Path("round") round: Int,
        @Path("lap") lap: Int
    ): RaceTableResponse

    @GET("f1/{year}/{round}/pitstops.json?limit=100")
    suspend fun getPitStops(
        @Path("year") year: Int,
        @Path("round") round: Int
    ): RaceTableResponse

    @GET("f1/{year}/constructors/{constructorId}/results.json")
    suspend fun getConstructorSeasonResults(
        @Path("year") year: Int,
        @Path("constructorId") constructorId: String
    ): RaceTableResponse
}
