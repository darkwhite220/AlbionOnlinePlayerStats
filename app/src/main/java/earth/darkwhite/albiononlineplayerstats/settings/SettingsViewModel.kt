package earth.darkwhite.albiononlineplayerstats.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import earth.darkwhite.albiononlineplayerstats.PreferencesManager
import earth.darkwhite.albiononlineplayerstats.database.MyDao
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val dataSource: MyDao
) : ViewModel() {

    init {
        Log.d(TAG, "init: ")
    }

    val settingsPreferences = preferencesManager.settingsPreferences

    fun onTrackDeviceThemeChange(value: String) {
        Log.d(TAG, "onTrackDeviceThemeChange: $value")
        viewModelScope.launch { preferencesManager.updateTrackDeviceTheme(value) }
    }

    fun onSendReportDataChange(boolean: Boolean) {
        Log.d(TAG, "onSendReportDataChange: $boolean")
        viewModelScope.launch { preferencesManager.updateSendReportData(boolean) }
    }

    fun onDeleteDataClick() {
        Log.d(TAG, "onDeleteDataClick: ")
        viewModelScope.launch {
            dataSource.clearAllPlayers()
            dataSource.clearAllReports()
            preferencesManager.updateRangeSliderValue(listOf(0F, 100F))
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}