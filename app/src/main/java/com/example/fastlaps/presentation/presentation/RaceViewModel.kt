package com.example.fastlaps.presentation.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.model.Session
import com.example.fastlaps.presentation.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RaceViewModel : ViewModel() {
    private val _sessions = MutableStateFlow<Map<Int, List<Session>>>(emptyMap())
    val sessions: StateFlow<Map<Int, List<Session>>> = _sessions

    // Estado para controlar qué carrera está expandida
    private val _expandedMeetingKey = MutableStateFlow<Int?>(null)
    val expandedMeetingKey: StateFlow<Int?> = _expandedMeetingKey

    fun toggleMeetingSessions(meetingKey: Int) {
        _expandedMeetingKey.value = if (_expandedMeetingKey.value == meetingKey) {
            null
        } else {
            meetingKey
        }
    }


    init {
        fetchSessions()
    }

    private fun fetchSessions() {
        viewModelScope.launch {
            try {
                Log.d("fetchSessions", "Fetching sessions for year 2025")
                val response = RetrofitInstance.api.getSessions(year = 2025)
                Log.d("fetchSessions", "Fetched ${response.size} sessions")

                val groupedSessions = response.groupBy { it.meeting_key }
                Log.d("fetchSessions", "Grouped sessions by meeting_key: ${groupedSessions.keys}")

                _sessions.value = groupedSessions
            } catch (e: Exception) {
                Log.e("fetchSessions", "Error fetching sessions: ${e.message}", e)
            }
        }
    }
}