package earth.darkwhite.albiononlineplayerstats.mainscreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import earth.darkwhite.albiononlineplayerstats.*
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.database.model.ReportContainer
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "MainScreenViewModel"

@ExperimentalCoroutinesApi
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repo: MainScreenRepo,
    application: Application,
    private val preferencesManager: PreferencesManager
) : AndroidViewModel(application) {

    init {
        Log.d(TAG, "Init: ")
    }

    // Count how many server responses failed
    private val _serverResponseFailed = MutableLiveData(-2)

    // If a request fail 3 times and loading is done then display fail snackBar
    // When loading is done +1 to start listening to _serverResponseFailed changes
    val displayFailLoadingSnackBar: LiveData<Int> = Transformations.map(_serverResponseFailed) { value ->
        Log.d(TAG, "displayFailLoadingSnackBar: $value && ${loadingProgress.value}")
        return@map if (value > 0 && loadingProgress.value == false) {
            value
        } else {
            0
        }
    }

    val filterPreferences = preferencesManager.filterPreferences

    // Display progressBar when both are true
    private val _deathsLoading = MutableStateFlow(false)
    private val _killsLoading = MutableStateFlow(false)

    // Uses: -To display ProgressBar, -To not fetch new data when reloading. -Display snackBar after loading and some requests fail
    val loadingProgress: LiveData<Boolean> = combine(_deathsLoading, _killsLoading) { _deathsLoading, _killsLoading ->
        Log.d(TAG, "loadingProgress: $_deathsLoading $_killsLoading")
        return@combine !(!_deathsLoading && !_killsLoading)
    }.asLiveData()

    // Save tracked players count, Prevent fetching new data when deleting players
    private val trackedPlayersCount = MutableStateFlow(0)

    // Query parameters
    private val searchQuery = MutableStateFlow(mutableListOf(""))

    // Fetch reports "PvPData" for DB
    private val reportsFlow =
        combine(
            searchQuery,
            filterPreferences
        ) { searchQuery, preferences ->
            Pair(searchQuery, preferences)
        }.flatMapLatest { (searchQuery, preferences) ->
            Log.d(TAG, "reportsFlow: ")
            repo.getPvPDataQuery(searchQuery, preferences)
        }

    // Reports from user filter
    val reports = reportsFlow.asLiveData()

    // Display when no reports available
    val displayReportsCount: LiveData<String> = Transformations.map(reports) { value: List<ReportsResponse> ->
        Log.d(TAG, "displayReportsCount: " + value.size)
        application.getString(R.string.numb_reports_found, value.size)
    }

    // Return LiveData list from database of tracked players data
    private val trackedUsersData: LiveData<List<ReportContainer>> = repo.trackedUsersData

    // Get min/max fame values
    val fameRangeSliderValues: LiveData<MutableList<Float>?> = Transformations.map(trackedUsersData) {
        Log.d(TAG, "fameRangeSliderValues: ")
        val list = mutableListOf(0F, 100F)
        it?.apply {
            if (it.isNotEmpty()) {
                list.clear()
                list.add(it.minOf { pvPData -> pvPData.fame.toFloat() }) // From
                list.add(it.maxOf { pvPData -> pvPData.fame.toFloat() }) // To
            }
        }
        onRangeSliderValueChange(list)
        return@map null
    }

    // Display no player/report is saved
    val reportsCountBool: LiveData<Boolean> = Transformations.map(trackedUsersData) {
        Log.d(TAG, "reportsCountBool: " + it?.size)
        return@map when (it?.size) {
            0 -> true
            else -> false
        }
    }

    // Return LiveData list from database of tracked players
    val trackedUsers: LiveData<List<Player>> = repo.trackedUsers

    // Display visibility based on tracked players number
    val playersCount: LiveData<Boolean> = Transformations.map(trackedUsers) {
        Log.d(TAG, "playersCount: " + it?.size)
        updateDBSearchQuery(it)
        return@map when (it?.size) {
            0 -> true
            else -> false
        }
    }

    // Update user searchQuery after Player DB change
    private fun updateDBSearchQuery(list: List<Player>) {
        val queryList = mutableListOf<String>()
        if (list.isEmpty()) {
            queryList.add("")
        } else {
            list.forEach {
                if (it.selected) {
                    queryList.add(it.name)
                    Log.d(TAG, "observeForeverTrackedUsers: ${it.name}")
                }
            }
        }
        // Update players query
        searchQuery.value = queryList
    }

    // Fetching new data or block it if its triggered by a player being deleted or modified
    fun fetchNewData() {
        Log.d(TAG, "fetchNewData: ${trackedUsers.value?.size!!} ${trackedPlayersCount.value}")
        if (trackedPlayersCount.value >= trackedUsers.value?.size!!) {
            trackedPlayersCount.value = trackedUsers.value?.size!!
            Log.d(TAG, "fetchNewData: blocked")
            return
        }
        trackedPlayersCount.value = trackedUsers.value?.size!!
        if (loadingProgress.value == false) {
            startRequestForNewReportsData()
        }
    }

    fun startRequestForNewReportsData() {
        Log.d(TAG, "startRequestForNewReportsData: ")
        restFailCounter()
        val idsList: MutableList<String> = arrayListOf()
        trackedUsers.value?.onEach {
            idsList.add(it.id)
        }
        if (idsList.size > 0) {
            startRequestForDeaths(idsList)
            startRequestForKills(idsList)
        }
    }

    private fun startRequestForDeaths(idList: MutableList<String>) {
        Log.d(TAG, "startRequestForDeaths: ")
        viewModelScope.launch {
            var idCount = 0
            idList.forEach { idString ->
                delay(50)
                launch {
                    Log.d(TAG, "startRequestForDeaths: ${Thread.currentThread().name}")
                    _deathsLoading.value = true
                    requestNewDeathsData(idString)
                        .retryWhen { cause, attempt ->
                            Log.d(TAG, "startRequestForDeaths: retry attempt $attempt cause ${cause.message}")
                            delay(1000)
                            return@retryWhen attempt < 2 // This will request 3 times
                        }
                        .catch {
                            Log.d(TAG, "startRequestForDeaths: error")
                            idCount = idCount.plus(1)
                            _serverResponseFailed.value = _serverResponseFailed.value?.plus(1)
                        }
                        .collect {
                            Log.d(TAG, "startRequestForDeaths: collect")
                            idCount = idCount.plus(1)
                        }
                    // Requests finished
                    if (idCount == idList.size) {
                        Log.d(TAG, "startRequestForDeaths: Requests finished")
                        _deathsLoading.value = false
                        _serverResponseFailed.value = _serverResponseFailed.value?.plus(1)
                    }
                }
            }
        }
    }

    private fun startRequestForKills(idList: MutableList<String>) {
        Log.d(TAG, "startRequestForKills: ")
        viewModelScope.launch {
            var idCount = 0
            idList.forEach { idString ->
                delay(50)
                launch {
                    Log.d(TAG, "startRequestForKills: ${Thread.currentThread().name}")
                    _killsLoading.value = true
                    requestNewKillsData(idString)
                        .retryWhen { cause, attempt ->
                            Log.d(TAG, "startRequestForKills: retry attempt $attempt cause ${cause.message}")
                            delay(1000)
                            return@retryWhen attempt < 2 // This will request 3 times
                        }
                        .catch {
                            Log.d(TAG, "startRequestForKills: error $this")
                            idCount = idCount.plus(1)
                            _serverResponseFailed.value = _serverResponseFailed.value?.plus(1)
                        }
                        .collect {
                            Log.d(TAG, "startRequestForKills: collect")
                            idCount = idCount.plus(1)
                        }
                    // Requests finished
                    if (idCount == idList.size) {
                        Log.d(TAG, "startRequestForKills: Requests finished")
                        _killsLoading.value = false
                        _serverResponseFailed.value = _serverResponseFailed.value?.plus(1)
                    }
                }
            }
        }
    }

    private fun requestNewDeathsData(string: String): Flow<List<ReportsResponse>> = flow {
        emit(repo.deaths(string))
    }

    private fun requestNewKillsData(string: String): Flow<List<ReportsResponse>> = flow {
        emit(repo.kills(string))
    }

    fun onReportTypeChange(reportType: ReportType) = viewModelScope.launch {
        preferencesManager.updateReportType(reportType)
    }

    fun onSortOrderChange(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onSortByChange(sortBy: SortBy) = viewModelScope.launch {
        preferencesManager.updateSortBy(sortBy)
    }

    // Range slider From/To values
    private fun onRangeSliderValueChange(list: List<Float>) = viewModelScope.launch {
        preferencesManager.updateRangeSliderValue(list)
    }

    // Fame query values (min/max)
    fun onFameValueChange(list: List<Float>) = viewModelScope.launch {
        preferencesManager.updateFameValue(list)
    }

    // Reset server response fails
    fun restFailCounter() {
        Log.d(TAG, "restFailCounter: reset")
        _serverResponseFailed.value = -2
    }

    fun deletePlayerAndHisReports(playerName: String) {
        viewModelScope.launch {
            repo.deletePlayerAndHisReports(playerName)
        }
    }

    fun addPlayer(player: Player) {
        viewModelScope.launch {
            Log.d(TAG, "addPlayer: " + player.name)
            repo.insertPlayer(player)
        }
    }

    fun updatePlayer(player: Player) {
        viewModelScope.launch {
            Log.d(TAG, "updatePlayer: " + player.name)
            repo.updatePlayer(player)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

}