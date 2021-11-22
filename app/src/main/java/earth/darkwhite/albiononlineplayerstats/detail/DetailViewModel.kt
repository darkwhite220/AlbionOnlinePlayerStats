package earth.darkwhite.albiononlineplayerstats.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "DetailViewModel"

class DetailViewModel(pvpData: ReportsResponse) : ViewModel() {

    private val _data = MutableStateFlow<ReportsResponse?>(null)
    val data: StateFlow<ReportsResponse?> = _data

    init {
        Log.d(TAG, "init: ")
        _data.value = pvpData
    }

}

class DetailViewModelFactory(private val data: ReportsResponse) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(data) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}