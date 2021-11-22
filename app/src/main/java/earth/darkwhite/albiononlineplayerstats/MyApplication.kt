package earth.darkwhite.albiononlineplayerstats

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MyApplication"

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")

        ProcessLifecycleOwner.get().lifecycleScope.launch {
            preferencesManager.settingsPreferences.first().apply {
                Log.d(TAG, "settingsPreferences: ")
                when (this.theme) {
                    "follow_device_theme" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    "dark_theme" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    "light_theme" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }

}