package com.example.fastlaps.presentation.presentation.viewmodel

import ConstructorStanding
import DriverStanding
import NewsModel
import NewsRepository
import QualifyingResult
import Race
import RaceResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class RaceViewModel() : ViewModel() {
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val driverStandingsRepository = DriverStandingsRepository()

    // Race schedule
    private val _races = MutableStateFlow<List<Race>>(emptyList())
    val races: StateFlow<List<Race>> = _races.asStateFlow()

    // Race results
    private val _raceResults = MutableStateFlow<List<RaceResult>>(emptyList())
    val raceResults: StateFlow<List<RaceResult>> = _raceResults.asStateFlow()

    // Qualifying results
    private val _qualifyingResults = MutableStateFlow<List<QualifyingResult>>(emptyList())
    val qualifyingResults: StateFlow<List<QualifyingResult>> = _qualifyingResults.asStateFlow()

    // Sprint results
    private val _sprintResults = MutableStateFlow<List<RaceResult>>(emptyList())
    val sprintResults: StateFlow<List<RaceResult>> = _sprintResults.asStateFlow()

    // 0 = Race, 1 = Qualifying, 2 = Sprint
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentRound = MutableStateFlow<Int?>(null)
    val currentRound: StateFlow<Int?> = _currentRound.asStateFlow()

    private val _currentRaceName = MutableStateFlow<String?>(null)
    val currentRaceName: StateFlow<String?> = _currentRaceName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    init {
        loadRaces()
    }

    fun loadRaces() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val races = driverStandingsRepository.getRaceSchedule(currentYear)
                _races.value = races
            } catch (e: Exception) {
                _errorState.value = when (e) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timeout"
                    else -> "Failed to load races: ${e.localizedMessage}"
                }
                Log.e("RaceViewModel", "Error fetching races", e)
                _races.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRaceResults(round: Int, raceName: String? = null) {
        _currentRound.value = round
        _currentRaceName.value = raceName
        _selectedTab.value = 0
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val resultsDeferred = async { driverStandingsRepository.getRaceResults(currentYear, round) }
                val qualifyingDeferred = async { driverStandingsRepository.getQualifyingResults(currentYear, round) }
                val sprintDeferred = async {
                    try { driverStandingsRepository.getSprintResults(currentYear, round) }
                    catch (_: Exception) { emptyList() }
                }

                val results = resultsDeferred.await()
                val qualifying = qualifyingDeferred.await()
                val sprint = sprintDeferred.await()

                _raceResults.value = results
                _qualifyingResults.value = qualifying
                _sprintResults.value = sprint

                if (results.isEmpty() && qualifying.isEmpty() && sprint.isEmpty()) {
                    _errorState.value = "No results available"
                }
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading race results", e)
                _raceResults.value = emptyList()
                _qualifyingResults.value = emptyList()
                _sprintResults.value = emptyList()
                _errorState.value = "Error loading results: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun retryLoading() {
        loadRaces()
    }

    fun setErrorState(message: String?) {
        _errorState.value = message
    }

    fun resetRaceResults() {
        _raceResults.value = emptyList()
        _qualifyingResults.value = emptyList()
        _sprintResults.value = emptyList()
        _selectedTab.value = 0
    }

    // Driver standings
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
                val response = driverStandingsRepository.getDriverStandings(currentYear)
                val standings = response.MRData.StandingsTable.StandingsLists
                    .firstOrNull()?.DriverStandings ?: emptyList()
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

    // Constructor standings
    private val _constructorStandings = MutableStateFlow<List<ConstructorStanding>>(emptyList())
    val constructorStandings: StateFlow<List<ConstructorStanding>> = _constructorStandings.asStateFlow()

    private val _isLoadingConstructors = MutableStateFlow(false)
    val isLoadingConstructors = _isLoadingConstructors.asStateFlow()

    private val _constructorErrorState = MutableStateFlow<String?>(null)
    val constructorErrorState = _constructorErrorState.asStateFlow()

    fun loadConstructorStandings(year: Int = currentYear) {
        viewModelScope.launch {
            _isLoadingConstructors.value = true
            _constructorErrorState.value = null
            try {
                val response = driverStandingsRepository.getConstructorStandings(year)
                _constructorStandings.value = response.MRData.StandingsTable.StandingsLists
                    .firstOrNull()?.ConstructorStandings ?: emptyList()
            } catch (e: Exception) {
                _constructorErrorState.value = "Error loading constructor standings: ${e.localizedMessage}"
                Log.e("RaceViewModel", "Error loading constructor standings", e)
            } finally {
                _isLoadingConstructors.value = false
            }
        }
    }

    // News
    private val newsRepository = NewsRepository()
    private val _news = MutableStateFlow<List<NewsModel>>(emptyList())
    val news: StateFlow<List<NewsModel>> = _news.asStateFlow()

    private val _isLoadingNews = MutableStateFlow(false)
    val isLoadingNews: StateFlow<Boolean> = _isLoadingNews.asStateFlow()

    private val _newsErrorState = MutableStateFlow<String?>(null)
    val newsErrorState: StateFlow<String?> = _newsErrorState.asStateFlow()

    fun loadNews(language: String) {
        viewModelScope.launch {
            _isLoadingNews.value = true
            _newsErrorState.value = null
            try {
                val newsResult = newsRepository.getF1News(language)
                _news.value = newsResult
                if (newsResult.isEmpty()) {
                    _newsErrorState.value = "No news available"
                }
            } catch (e: Exception) {
                _newsErrorState.value = when (e) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timeout"
                    else -> "Failed to load news: ${e.localizedMessage}"
                }
                Log.e("RaceViewModel", "Error loading news", e)
                _news.value = emptyList()
            } finally {
                _isLoadingNews.value = false
            }
        }
    }

    fun retryLoadingNews(language: String) {
        loadNews(language)
    }
}
