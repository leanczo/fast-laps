package com.example.fastlaps.presentation.domain.repository

import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.model.PositionData
import com.example.fastlaps.presentation.model.Session
import com.example.fastlaps.presentation.network.RetrofitInstance

class SessionRepository {
    private val api = RetrofitInstance.api

    suspend fun getSessions(year: Int): List<Session> {
        return api.getSessions(year)
    }

    suspend fun getSessionPositions(sessionKey: Int): List<PositionData> {
        return api.getSessionPositions(sessionKey)
    }

    fun processFinalPositions(positions: List<PositionData>): List<FinalPosition> {
        return positions.groupBy { it.driver_number }
            .map { (driverNumber, positions) ->
                val lastPosition = positions.maxByOrNull { it.date }
                FinalPosition(
                    driverNumber = driverNumber,
                    position = lastPosition?.position ?: 0,
                    timestamp = lastPosition?.date ?: ""
                )
            }
            .sortedBy { it.position }
    }
}