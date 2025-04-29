package com.example.fastlaps.presentation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.model.Result
import com.example.fastlaps.presentation.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RaceViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<Result>>(emptyList())
    val results: StateFlow<List<Result>> = _results

    init {
        fetchResults()
    }

    private fun fetchResults() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getLastRaceResults()
                println("Respuesta completa: $response") // Ver en Logcat
                _results.value = response.MRData.RaceTable.Races.firstOrNull()?.Results ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
