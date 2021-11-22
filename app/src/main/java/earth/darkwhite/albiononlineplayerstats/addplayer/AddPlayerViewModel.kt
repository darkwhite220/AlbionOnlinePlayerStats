package earth.darkwhite.albiononlineplayerstats.addplayer

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import earth.darkwhite.albiononlineplayerstats.RequestStatus
import earth.darkwhite.albiononlineplayerstats.data.api.model.PlayerData
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "AddPlayerViewModel"

@HiltViewModel
class AddPlayerViewModel @Inject constructor(
    private val repo: AddPlayerRepo
) : ViewModel() {

    // Hold selected player
    private val _selectedPlayer = MutableStateFlow<Player?>(null)

    private val _status = MutableStateFlow(RequestStatus.DONE)
    val status: StateFlow<RequestStatus> = _status

    private val _progressBarVisibility = MutableStateFlow(false)
    val progressBarVisibility: StateFlow<Boolean> = _progressBarVisibility

    private val _textViewVisibility = MutableStateFlow(false)
    val textViewVisibility: StateFlow<Boolean> = _textViewVisibility

    private val _firstSearchData = MutableStateFlow<List<PlayerData>?>(null)
    val firstSearchData: StateFlow<List<PlayerData>?> = _firstSearchData

    private val _fetchStatus = MutableStateFlow("")
    val fetchStatus: StateFlow<String> = _fetchStatus

    init {
        Log.d(TAG, "init: ")
    }

    fun initFetchData(query: String) {
        Log.d(TAG, "initFetchData: ")
        _fetchStatus.value = "Loading..."
        viewModelScope.launch {
            fetchData(query = query)
                .retryWhen { cause, attempt -> // Retry 3 times
                    onRetry(cause = cause, attempts = attempt)
                    delay(1000)
                    return@retryWhen attempt < 2
                }.catch {
                    onCatch()
                }.collect { response ->
                    onCollect(response, query)
                }
        }
    }

    private fun fetchData(query: String): Flow<List<PlayerData>?> = flow {
        _status.value = RequestStatus.LOADING
        _progressBarVisibility.value = true
        _textViewVisibility.value = true
        _firstSearchData.value = null
        emit(repo.fetchQuery(query = query))
    }

    private fun onRetry(cause: Throwable, attempts: Long) {
        Log.d(TAG, "onRetry: ${cause.message.toString()}, $attempts")
        _fetchStatus.value = "Attempt ${attempts.plus(1)}/3 failed, Cause: ${cause.message.toString()}"
    }

    private fun onCatch() {
        Log.d(TAG, "onCatch: ")
        _progressBarVisibility.value = false
        _status.value = RequestStatus.DONE
    }

    private fun onCollect(response: List<PlayerData>?, query: String) {
        Log.d(TAG, "onCollect: ")
        _firstSearchData.value = response
        _status.value = RequestStatus.DONE
        _progressBarVisibility.value = false
        if (response?.size!! == 0) {
            _fetchStatus.value = "No player matching \"$query\" found."
        } else {
            _textViewVisibility.value = false
        }
    }

    fun onPlayerSelected(player: Player) {
        Log.d(TAG, "onPlayerSelected: ")
        _selectedPlayer.value = player
    }

    /**
     * Fetch players list in DB, wait on player selected then add if doesn't exist
     */
    val checkPlayer: Flow<Boolean?> = repo.getPlayers.combine(_selectedPlayer) { playersList, selectedPlayer ->
        Log.d(TAG, "checkPlayer: ")
        if (selectedPlayer == null) {
            return@combine null
        }
        return@combine checkPlayerMatch(playersList, selectedPlayer)
    }

    private fun checkPlayerMatch(playersList: List<Player>, selectedPlayer: Player): Boolean {
        Log.d(TAG, "checkPlayerMatch: ")
        return playersList.firstOrNull {
            it.id == selectedPlayer.id
        }.run {
            return@run when (this) {
                null -> {
                    addPlayer(selectedPlayer)
                    false
                }
                else -> true
            }
        }
    }

    fun resetPlayerValue() {
        Log.d(TAG, "resetPlayerValue: ")
        _selectedPlayer.value = null
    }

    /**
     * Add player to DB
     */
    fun addPlayer(player: Player) {
        viewModelScope.launch {
            Log.d(TAG, "addPlayer: " + player.name)
            repo.insertPlayer(player)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

}
