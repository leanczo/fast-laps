package com.example.fastlaps.presentation.presentation.viewmodel

import ConstructorStanding
import DriverStanding
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.domain.repository.SessionRepository
import com.example.fastlaps.presentation.model.Driver
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.model.Session
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// RaceViewModel.kt
class RaceViewModel() : ViewModel() {
    private val sessionRepository = SessionRepository()
    private val driverStandingsRepository = DriverStandingsRepository()

    private val _finalPositions = MutableStateFlow<List<FinalPosition>>(emptyList())
    val finalPositions: StateFlow<List<FinalPosition>> = _finalPositions.asStateFlow()

    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers: StateFlow<List<Driver>> = _drivers.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentSessionKey = MutableStateFlow<Int?>(null)
    val currentSessionKey: StateFlow<Int?> = _currentSessionKey.asStateFlow()

    var simulateError = false
    var simulateEmpty = false

    fun loadSessionData(sessionKey: Int) {
        _currentSessionKey.value = sessionKey
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                // Cargar posiciones y pilotos en paralelo
                val positionsDeferred = async { sessionRepository.getSessionPositions(sessionKey) }
                val driversDeferred = async { sessionRepository.getSessionDrivers(sessionKey) }

                val positions = positionsDeferred.await()
                val drivers = driversDeferred.await()

                _drivers.value = drivers
                _finalPositions.value = sessionRepository.processFinalPositions(positions, drivers)

            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading session data", e)
                _finalPositions.value = emptyList()
                _errorState.value = "Error loading session data: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    // Función mejorada para cargar sesiones
    fun loadSessions(year: Int = 2025) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorState.value = null

                if (simulateError) {
                    throw Exception("Simulated API error")
                }

                val response = if (simulateEmpty) emptyList() else sessionRepository.getSessions(year)
                _sessions.value = response.groupBy { it.meeting_key }

            } catch (e: Exception) {
                _errorState.value = when (e) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timeout"
                    else -> "Failed to load sessions: ${e.localizedMessage}"
                }
                Log.e("RaceViewModel", "Error fetching sessions", e)
                _sessions.value = emptyMap()
            } finally {
                _isLoading.value = false
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
                val response = sessionRepository.getSessions(2025)
                _sessions.value = response.groupBy { it.meeting_key }
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error fetching sessions", e)
            }
        }
    }

    fun retryLoading() {
        loadSessions()
    }

    fun setErrorState(message: String?) {
        _errorState.value = message
    }

    fun getErrorState(): String? {
        return _errorState.value
    }

    // 3. Función para expandir/contraer sesiones
    fun toggleMeetingSessions(meetingKey: Int) {
        _expandedMeetingKey.value = if (_expandedMeetingKey.value == meetingKey) {
            null
        } else {
            meetingKey
        }
    }

    fun resetSessionResults() {
        _finalPositions.value = emptyList()
    }

    init {
        fetchSessions() // Cargar sesiones al inicializar
        //loadDriverStandings()
    }


    private val _driverStandings = MutableStateFlow<List<DriverStanding>>(emptyList())
    val driverStandings = _driverStandings.asStateFlow()

    private val _isLoadingDrivers = MutableStateFlow(false)
    val isLoadingDrivers = _isLoadingDrivers.asStateFlow()

    private val _driverErrorState = MutableStateFlow<String?>(null)
    val driverErrorState = _driverErrorState.asStateFlow()

    fun loadDriverStandings() {
        viewModelScope.launch {
            _isLoadingDrivers.value = true
            _driverErrorState.value = null

            try {
                val response = driverStandingsRepository.getDriverStandings(2025)
                val standings = response.MRData.StandingsTable.StandingsLists.first().DriverStandings
                _driverStandings.value = standings

                if (standings.isEmpty()) {
                    _driverErrorState.value = "No driver standings available"
                }
            } catch (e: Exception) {
                _driverErrorState.value = "Failed to load driver standings: ${e.localizedMessage}"
                Log.e("RaceViewModel", "Error loading driver standings", e)
            } finally {
                _isLoadingDrivers.value = false
            }
        }
    }



    // Nuevos estados para constructores
    private val _constructorStandings = MutableStateFlow<List<ConstructorStanding>>(emptyList())
    val constructorStandings: StateFlow<List<ConstructorStanding>> = _constructorStandings.asStateFlow()

    private val _isLoadingConstructors = MutableStateFlow(false)
    val isLoadingConstructors = _isLoadingConstructors.asStateFlow()

    private val _constructorErrorState = MutableStateFlow<String?>(null)
    val constructorErrorState = _constructorErrorState.asStateFlow()

    // Función para cargar datos
    fun loadConstructorStandings(year: Int = 2025) {
        viewModelScope.launch {
            _isLoadingConstructors.value = true
            _constructorErrorState.value = null
            try {
                val response = driverStandingsRepository.getConstructorStandings(year)
                _constructorStandings.value = response.MRData.StandingsTable.StandingsLists.first().ConstructorStandings
            } catch (e: Exception) {
                _constructorErrorState.value = "Error loading constructor standings: ${e.localizedMessage}"
                Log.e("RaceViewModel", "Error loading constructor standings", e)
            } finally {
                _isLoadingConstructors.value = false
            }
        }
    }
}