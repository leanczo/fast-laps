package com.example.fastlaps.presentation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.domain.repository.SessionRepository
import com.example.fastlaps.presentation.model.Driver
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.model.Session
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// RaceViewModel.kt
class RaceViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _finalPositions = MutableStateFlow<List<FinalPosition>>(emptyList())
    val finalPositions: StateFlow<List<FinalPosition>> = _finalPositions.asStateFlow()

    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers: StateFlow<List<Driver>> = _drivers.asStateFlow()

    fun loadSessionData(sessionKey: Int) {
        viewModelScope.launch {
            try {
                // Cargar posiciones y pilotos en paralelo
                val positionsDeferred = async { repository.getSessionPositions(sessionKey) }
                val driversDeferred = async { repository.getSessionDrivers(sessionKey) }

                val positions = positionsDeferred.await()
                val drivers = driversDeferred.await()

                _drivers.value = drivers

                // Procesar posiciones y combinar con info de pilotos
                val processedPositions = repository.processFinalPositions(positions).map { position ->
                    position.copy(
                        driverInfo = drivers.find { it.driver_number == position.driverNumber }
                    )
                }

                _finalPositions.value = processedPositions
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading session data", e)
                _finalPositions.value = emptyList()
            }
        }
    }

    // 1. Definición correcta de StateFlows
    private val _sessions = MutableStateFlow<Map<Int, List<Session>>>(emptyMap())
    val sessions: StateFlow<Map<Int, List<Session>>> = _sessions.asStateFlow()

    private val _expandedMeetingKey = MutableStateFlow<Int?>(null)
    val expandedMeetingKey: StateFlow<Int?> = _expandedMeetingKey.asStateFlow()

    // 2. Función para cargar sesiones
    fun fetchSessions() {
        viewModelScope.launch {
            try {
                val response = repository.getSessions(2025) // Año actualizado
                _sessions.value = response.groupBy { it.meeting_key }
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error fetching sessions", e)
            }
        }
    }

    // 3. Función para expandir/contraer sesiones
    fun toggleMeetingSessions(meetingKey: Int) {
        _expandedMeetingKey.value = if (_expandedMeetingKey.value == meetingKey) {
            null
        } else {
            meetingKey
        }
    }

    init {
        fetchSessions() // Cargar sesiones al inicializar
    }
}