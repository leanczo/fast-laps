package com.example.fastlaps.presentation.domain.repository

import com.example.fastlaps.presentation.model.Driver
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

    suspend fun getSessionDrivers(sessionKey: Int): List<Driver> {
        return api.getSessionDrivers(sessionKey)
    }

    fun processFinalPositions(
        positions: List<PositionData>,
        drivers: List<Driver> = emptyList()
    ): List<FinalPosition> {
        // 1. Obtener la última posición de cada piloto
        val lastPositions = positions
            .groupBy { it.driver_number }
            .mapNotNull { (driverNumber, positions) ->
                positions.maxByOrNull { it.date }?.let { lastPosition ->
                    val driver = drivers.find { it.driver_number == driverNumber }
                    FinalPosition(
                        driverNumber = driverNumber,
                        position = lastPosition.position,
                        timestamp = lastPosition.date,
                        driverInfo = driver
                    )
                }
            }

        // 2. Ordenar por posición real
        val sortedByRacePosition = lastPositions.sortedBy { it.position }

        // 3. Asignar posiciones secuenciales estrictas (1, 2, 3, 4...)
        return assignStrictPositions(sortedByRacePosition)
    }

    /**
     * Asigna posiciones secuenciales estrictas (1, 2, 3, 4...)
     * ignorando empates en los datos originales
     */
    private fun assignStrictPositions(positions: List<FinalPosition>): List<FinalPosition> {
        return positions.mapIndexed { index, position ->
            position.copy(position = index + 1)
        }
    }
}