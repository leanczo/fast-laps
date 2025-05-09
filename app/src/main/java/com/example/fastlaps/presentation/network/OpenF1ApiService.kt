package com.example.fastlaps.presentation.network

import com.example.fastlaps.presentation.model.PositionData
import com.example.fastlaps.presentation.model.Session
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenF1ApiService {
    @GET("v1/sessions")
    suspend fun getSessions(@Query("year") year: Int): List<Session>

    @GET("v1/position")
    suspend fun getSessionPositions(
        @Query("session_key") sessionKey: Int
    ): List<PositionData>
}
