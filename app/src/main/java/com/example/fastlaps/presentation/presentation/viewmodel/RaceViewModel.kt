package com.example.fastlaps.presentation.presentation.viewmodel

import ConstructorStanding
import DriverStanding
import NewsModel
import NewsRepository
import Lap
import PitStop
import QualifyingResult
import Race
import RaceResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastlaps.presentation.repository.DriverStandingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class RaceViewModel() : ViewModel() {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val _selectedYear = MutableStateFlow(currentYear)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()
    private val driverStandingsRepository = DriverStandingsRepository()

    fun setSelectedYear(year: Int) {
        if (_selectedYear.value != year) {
            _selectedYear.value = year
            _races.value = emptyList()
            racesLastFetched = 0L
            _driverStandings.value = emptyList()
            driverStandingsLastFetched = 0L
            _constructorStandings.value = emptyList()
            constructorStandingsLastFetched = 0L
            _allSeasonResults.value = emptyList()
            loadRaces()
        }
    }

    // Cache timestamps
    private var racesLastFetched = 0L
    private var driverStandingsLastFetched = 0L
    private var constructorStandingsLastFetched = 0L
    private var newsLastFetched = 0L
    private var newsLastLanguage: String? = null

    companion object {
        private const val CACHE_RACES_MS = 30 * 60 * 1000L
        private const val CACHE_STANDINGS_MS = 10 * 60 * 1000L
        private const val CACHE_NEWS_MS = 5 * 60 * 1000L
    }

    private fun isCacheValid(lastFetched: Long, durationMs: Long): Boolean {
        return System.currentTimeMillis() - lastFetched < durationMs
    }

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

    // Pit stops
    private val _pitStops = MutableStateFlow<List<PitStop>>(emptyList())
    val pitStops: StateFlow<List<PitStop>> = _pitStops.asStateFlow()

    // 0 = Race, 1 = Qualifying, 2 = Sprint
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentRound = MutableStateFlow<Int?>(null)
    val currentRound: StateFlow<Int?> = _currentRound.asStateFlow()

    private val _currentRaceName = MutableStateFlow<String?>(null)
    val currentRaceName: StateFlow<String?> = _currentRaceName.asStateFlow()

    private val _currentCircuitId = MutableStateFlow<String?>(null)
    val currentCircuitId: StateFlow<String?> = _currentCircuitId.asStateFlow()

    // Race replay - load one lap at a time
    private val _currentLapData = MutableStateFlow<Lap?>(null)
    val currentLapData: StateFlow<Lap?> = _currentLapData.asStateFlow()

    private val _prevLapData = MutableStateFlow<Lap?>(null)
    val prevLapData: StateFlow<Lap?> = _prevLapData.asStateFlow()

    private val _isLoadingLaps = MutableStateFlow(false)
    val isLoadingLaps: StateFlow<Boolean> = _isLoadingLaps.asStateFlow()

    private val _currentLap = MutableStateFlow(1)
    val currentLap: StateFlow<Int> = _currentLap.asStateFlow()

    private val _totalLaps = MutableStateFlow(0)
    val totalLaps: StateFlow<Int> = _totalLaps.asStateFlow()

    private var _replayRound = 0

    fun initReplay(round: Int) {
        _replayRound = round
        _currentLap.value = 1
        val lapsFromResults = _raceResults.value.firstOrNull()?.laps?.toIntOrNull()
        _totalLaps.value = if (lapsFromResults != null && lapsFromResults > 0) lapsFromResults else 70
        Log.d("RaceViewModel", "initReplay round=$round totalLaps=${_totalLaps.value}")
        loadLap(1)
    }

    fun changeLap(delta: Int) {
        if (_isLoadingLaps.value) return
        val max = _totalLaps.value.coerceAtLeast(1)
        val newLap = (_currentLap.value + delta).coerceIn(1, max)
        if (newLap != _currentLap.value) {
            _currentLap.value = newLap
            loadLap(newLap)
        }
    }

    private fun loadLap(lap: Int) {
        viewModelScope.launch {
            _isLoadingLaps.value = true
            try {
                val current = withContext(Dispatchers.IO) {
                    driverStandingsRepository.getSingleLap(_selectedYear.value, _replayRound, lap)
                }
                val prev = if (lap > 1) {
                    withContext(Dispatchers.IO) {
                        driverStandingsRepository.getSingleLap(_selectedYear.value, _replayRound, lap - 1)
                    }
                } else null
                _currentLapData.value = current
                _prevLapData.value = prev
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading lap $lap", e)
                _currentLapData.value = null
            } finally {
                _isLoadingLaps.value = false
            }
        }
    }

    // All season results (for fastest laps)
    private val _allSeasonResults = MutableStateFlow<List<Race>>(emptyList())
    val allSeasonResults: StateFlow<List<Race>> = _allSeasonResults.asStateFlow()

    private val _isLoadingFastestLaps = MutableStateFlow(false)
    val isLoadingFastestLaps: StateFlow<Boolean> = _isLoadingFastestLaps.asStateFlow()

    fun loadAllSeasonResults() {
        if (_allSeasonResults.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoadingFastestLaps.value = true
            try {
                val response = driverStandingsRepository.getAllSeasonResults(_selectedYear.value)
                _allSeasonResults.value = response
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading all season results", e)
            } finally {
                _isLoadingFastestLaps.value = false
            }
        }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    init {
        loadRaces()
    }

    fun loadRaces(forceRefresh: Boolean = false) {
        if (!forceRefresh && _races.value.isNotEmpty() && isCacheValid(racesLastFetched, CACHE_RACES_MS)) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val races = driverStandingsRepository.getRaceSchedule(_selectedYear.value)
                _races.value = races
                racesLastFetched = System.currentTimeMillis()
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
        _currentCircuitId.value = _races.value.find { it.round == round.toString() }?.Circuit?.circuitId
        _selectedTab.value = 0
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val resultsDeferred = async { driverStandingsRepository.getRaceResults(_selectedYear.value, round) }
                val qualifyingDeferred = async { driverStandingsRepository.getQualifyingResults(_selectedYear.value, round) }
                val sprintDeferred = async {
                    try { driverStandingsRepository.getSprintResults(_selectedYear.value, round) }
                    catch (_: Exception) { emptyList() }
                }
                val pitStopsDeferred = async {
                    try { driverStandingsRepository.getPitStops(_selectedYear.value, round) }
                    catch (_: Exception) { emptyList() }
                }

                val results = resultsDeferred.await()
                val qualifying = qualifyingDeferred.await()
                val sprint = sprintDeferred.await()
                val pitStops = pitStopsDeferred.await()

                _raceResults.value = results
                _qualifyingResults.value = qualifying
                _sprintResults.value = sprint
                _pitStops.value = pitStops

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
        loadRaces(forceRefresh = true)
    }

    fun setErrorState(message: String?) {
        _errorState.value = message
    }

    fun resetRaceResults() {
        _raceResults.value = emptyList()
        _qualifyingResults.value = emptyList()
        _sprintResults.value = emptyList()
        _pitStops.value = emptyList()
        _selectedTab.value = 0
        _currentCircuitId.value = null
    }

    // Driver detail
    private val _selectedDriver = MutableStateFlow<DriverStanding?>(null)
    val selectedDriver: StateFlow<DriverStanding?> = _selectedDriver.asStateFlow()

    private val _driverSeasonResults = MutableStateFlow<List<Race>>(emptyList())
    val driverSeasonResults: StateFlow<List<Race>> = _driverSeasonResults.asStateFlow()

    private val _isLoadingDriverDetail = MutableStateFlow(false)
    val isLoadingDriverDetail: StateFlow<Boolean> = _isLoadingDriverDetail.asStateFlow()

    fun loadDriverDetail(driverId: String) {
        _selectedDriver.value = _driverStandings.value.find { it.Driver.driverId == driverId }
        viewModelScope.launch {
            _isLoadingDriverDetail.value = true
            try {
                val results = driverStandingsRepository.getDriverSeasonResults(_selectedYear.value, driverId)
                _driverSeasonResults.value = results
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading driver results", e)
                _driverSeasonResults.value = emptyList()
            } finally {
                _isLoadingDriverDetail.value = false
            }
        }
    }

    // Driver standings
    private val _driverStandings = MutableStateFlow<List<DriverStanding>>(emptyList())
    val driverStandings = _driverStandings.asStateFlow()

    private val _isLoadingDrivers = MutableStateFlow(false)
    val isLoadingDrivers = _isLoadingDrivers.asStateFlow()

    private val _driverErrorState = MutableStateFlow<String?>(null)
    val driverErrorState = _driverErrorState.asStateFlow()

    fun loadDriverStandings(forceRefresh: Boolean = false) {
        if (!forceRefresh && _driverStandings.value.isNotEmpty() && isCacheValid(driverStandingsLastFetched, CACHE_STANDINGS_MS)) return
        viewModelScope.launch {
            _isLoadingDrivers.value = true
            _driverErrorState.value = null
            try {
                val response = driverStandingsRepository.getDriverStandings(_selectedYear.value)
                val standings = response.MRData.StandingsTable.StandingsLists
                    .firstOrNull()?.DriverStandings ?: emptyList()
                _driverStandings.value = standings
                driverStandingsLastFetched = System.currentTimeMillis()
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

    // Head to head
    private val _h2hDriver1Results = MutableStateFlow<List<Race>>(emptyList())
    val h2hDriver1Results: StateFlow<List<Race>> = _h2hDriver1Results.asStateFlow()

    private val _h2hDriver2Results = MutableStateFlow<List<Race>>(emptyList())
    val h2hDriver2Results: StateFlow<List<Race>> = _h2hDriver2Results.asStateFlow()

    private val _isLoadingH2H = MutableStateFlow(false)
    val isLoadingH2H: StateFlow<Boolean> = _isLoadingH2H.asStateFlow()

    fun loadHeadToHead(constructorId: String) {
        val teamDrivers = _driverStandings.value.filter {
            it.Constructors.any { c -> c.constructorId == constructorId }
        }
        _constructorDrivers.value = teamDrivers
        if (teamDrivers.size < 2) return

        viewModelScope.launch {
            _isLoadingH2H.value = true
            try {
                val d1 = async { driverStandingsRepository.getDriverSeasonResults(_selectedYear.value, teamDrivers[0].Driver.driverId) }
                val d2 = async { driverStandingsRepository.getDriverSeasonResults(_selectedYear.value, teamDrivers[1].Driver.driverId) }
                _h2hDriver1Results.value = d1.await()
                _h2hDriver2Results.value = d2.await()
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading H2H", e)
                _h2hDriver1Results.value = emptyList()
                _h2hDriver2Results.value = emptyList()
            } finally {
                _isLoadingH2H.value = false
            }
        }
    }

    // Constructor detail
    private val _selectedConstructor = MutableStateFlow<ConstructorStanding?>(null)
    val selectedConstructor: StateFlow<ConstructorStanding?> = _selectedConstructor.asStateFlow()

    private val _constructorSeasonResults = MutableStateFlow<List<Race>>(emptyList())
    val constructorSeasonResults: StateFlow<List<Race>> = _constructorSeasonResults.asStateFlow()

    private val _constructorDrivers = MutableStateFlow<List<DriverStanding>>(emptyList())
    val constructorDrivers: StateFlow<List<DriverStanding>> = _constructorDrivers.asStateFlow()

    private val _isLoadingConstructorDetail = MutableStateFlow(false)
    val isLoadingConstructorDetail: StateFlow<Boolean> = _isLoadingConstructorDetail.asStateFlow()

    fun loadConstructorDetail(constructorId: String) {
        _selectedConstructor.value = _constructorStandings.value.find { it.Constructor.constructorId == constructorId }
        viewModelScope.launch {
            _isLoadingConstructorDetail.value = true
            try {
                // Ensure driver standings are loaded for the drivers section
                if (_driverStandings.value.isEmpty()) {
                    val response = driverStandingsRepository.getDriverStandings(_selectedYear.value)
                    _driverStandings.value = response.MRData.StandingsTable.StandingsLists
                        .firstOrNull()?.DriverStandings ?: emptyList()
                }
                _constructorDrivers.value = _driverStandings.value.filter {
                    it.Constructors.any { c -> c.constructorId == constructorId }
                }
                val results = driverStandingsRepository.getConstructorSeasonResults(_selectedYear.value, constructorId)
                _constructorSeasonResults.value = results
            } catch (e: Exception) {
                Log.e("RaceViewModel", "Error loading constructor results", e)
                _constructorSeasonResults.value = emptyList()
            } finally {
                _isLoadingConstructorDetail.value = false
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

    fun loadConstructorStandings(forceRefresh: Boolean = false) {
        if (!forceRefresh && _constructorStandings.value.isNotEmpty() && isCacheValid(constructorStandingsLastFetched, CACHE_STANDINGS_MS)) return
        viewModelScope.launch {
            _isLoadingConstructors.value = true
            _constructorErrorState.value = null
            try {
                val response = driverStandingsRepository.getConstructorStandings(_selectedYear.value)
                _constructorStandings.value = response.MRData.StandingsTable.StandingsLists
                    .firstOrNull()?.ConstructorStandings ?: emptyList()
                constructorStandingsLastFetched = System.currentTimeMillis()
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

    fun loadNews(language: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && _news.value.isNotEmpty() && newsLastLanguage == language && isCacheValid(newsLastFetched, CACHE_NEWS_MS)) return
        viewModelScope.launch {
            _isLoadingNews.value = true
            _newsErrorState.value = null
            try {
                val newsResult = newsRepository.getF1News(language)
                _news.value = newsResult
                newsLastFetched = System.currentTimeMillis()
                newsLastLanguage = language
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
        loadNews(language, forceRefresh = true)
    }
}
