package earth.darkwhite.albiononlineplayerstats.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import earth.darkwhite.albiononlineplayerstats.R
import earth.darkwhite.albiononlineplayerstats.displaySnackBar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SettingsFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var themeListPref: ListPreference
    private lateinit var sendDataSwitch: SwitchPreferenceCompat
    private lateinit var deleteUserData: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        Log.d(TAG, "onCreatePreferences: ")
        initViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settingsPreferences.collect { preferences ->
                onSettingsPrefLoaded(preferences.theme)
            }
        }
    }

    private fun initViews() {
        Log.d(TAG, "initViews: ")
        themeListPref = findPreference("app_theme")!!
        themeListPref.onPreferenceChangeListener = onTrackDevicePreferenceChangeListener()
        sendDataSwitch = findPreference("crash_report")!!
        sendDataSwitch.onPreferenceChangeListener = onSendDataPreferenceChangeListener()
        deleteUserData = findPreference("delete_user_data")!!
        deleteUserData.onPreferenceClickListener = onDeleteDataClickListener()
    }

    private fun onTrackDevicePreferenceChangeListener(): Preference.OnPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            val value = newValue as String
            Log.d(TAG, "onTrackDevicePreferenceChangeListener: $value")
            viewModel.onTrackDeviceThemeChange(value)
            when (value) {
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
            onSettingsPrefLoaded(value)
            return@OnPreferenceChangeListener true
        }

    private fun onSendDataPreferenceChangeListener(): Preference.OnPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            Log.d(TAG, "onSendDataPreferenceChangeListener: $value")
            viewModel.onSendReportDataChange(value)
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(value)
            return@OnPreferenceChangeListener true
        }

    private fun onDeleteDataClickListener(): Preference.OnPreferenceClickListener =
        Preference.OnPreferenceClickListener {
            Log.d(TAG, "onDeleteDataClickListener: ")
            viewModel.onDeleteDataClick()
            displaySnackBar(this.requireView(), "Data delete")
            return@OnPreferenceClickListener true
        }

    private fun onSettingsPrefLoaded(value: String) {
        Log.d(TAG, "onSettingsPrefLoaded: ")
        when (value) {
            "follow_device_theme" -> {
                themeListPref.summary = this.getString(R.string.follow_device_theme)
            }
            "dark_theme" -> {
                themeListPref.summary = this.getString(R.string.dark_theme)
            }
            "light_theme" -> {
                themeListPref.summary = this.getString(R.string.light_theme)
            }
        }
    }

}