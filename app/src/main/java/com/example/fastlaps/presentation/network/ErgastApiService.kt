package com.example.fastlaps.presentation.network

import com.example.fastlaps.presentation.model.ErgastResponse
import retrofit2.http.GET

interface ErgastApiService {
    @GET("api/f1/current/last/results.json")
    suspend fun getLastRaceResults(): ErgastResponse
}