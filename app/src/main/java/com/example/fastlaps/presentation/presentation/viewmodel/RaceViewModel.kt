package com.example.fastlaps.presentation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.domain.repository.SessionRepository
import com.example.fastlaps.presentation.model.FinalPosition
import com.example.fastlaps.presentation.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// RaceViewModel.kt
class RaceViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _sessions = MutableStateFlow<Map<Int, List<Session>>>(emptyMap())
    val sessions: StateFlow<Map<Int, List<Session>>> = _sessions

    private val _finalPositions = MutableStateFlow<List<FinalPosition>>(emptyList())
    val finalPositions: StateFlow<List<FinalPosition>> = _finalPositions

    private val _expandedMeetingKey = MutableStateFlow<Int?>(null)
    val expandedMeetingKey: StateFlow<Int?> = _expandedMeetingKey

    init {
        fetchSessions()
    }

    private fun fetchSessions() {
        viewModelScope.launch {
            try {
                val response = repository.getSessions(2025)
                _sessions.value = response.groupBy { it.meeting_key }
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error fetching sessions", e)
            }
        }
    }

    fun toggleMeetingSessions(meetingKey: Int) {
        _expandedMeetingKey.value = if (_expandedMeetingKey.value == meetingKey) {
            null
        } else {
            meetingKey
        }
    }

    fun loadSessionResults(sessionKey: Int) {
        viewModelScope.launch {
            try {
                val positions = repository.getSessionPositions(sessionKey)
                _finalPositions.value = repository.processFinalPositions(positions)
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading positions", e)
            }
        }
    }
}