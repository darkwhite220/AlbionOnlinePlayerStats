package earth.darkwhite.albiononlineplayerstats

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class ReportType { BOTH, KILLS, DEATHS }
enum class SortOrder { ASC, DESC }
enum class SortBy { DATE, FAME }

data class FilterPreferences(
    val reportType: ReportType,
    val sortOrder: SortOrder,
    val sortBy: SortBy,
    val form: Float,
    val to: Float,
    val minStep: Float,
    val maxStep: Float,
    val stepSize: Float
)

data class SettingsPreferences(
    val theme: String,
    val sendReportData: Boolean
)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("albion_online_player_stats_app_preferences")

    val filterPreferences = dataStore.data
        .catch { exception ->
            Log.e(TAG, "error: ", exception)
            onCatch(exception)
        }
        .map { preferences ->
            val reportType = ReportType.valueOf(preferences[PreferencesKeys.REPORT_TYPE] ?: ReportType.BOTH.name)
            val sortOrder = SortOrder.valueOf(preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.DESC.name)
            val sortBy = SortBy.valueOf(preferences[PreferencesKeys.SORT_BY] ?: SortBy.DATE.name)
            val form = preferences[PreferencesKeys.SLIDER_FROM] ?: 0F
            val to = preferences[PreferencesKeys.SLIDER_TO] ?: 100F
            val min = preferences[PreferencesKeys.SLIDER_MIN_STEP] ?: 0F
            val max = preferences[PreferencesKeys.SLIDER_MAX_STEP] ?: 100F
            val stepSize = preferences[PreferencesKeys.SLIDER_STEP_SIZE] ?: 10F

            return@map FilterPreferences(
                reportType = reportType,
                sortOrder = sortOrder,
                sortBy = sortBy,
                form, to, min, max, stepSize
            )
        }

    /**
     * Update Filter
     */
    suspend fun updateReportType(reportType: ReportType) {
        dataStore.edit { preferences ->
            Log.d(TAG, "updateReportType: ${reportType.name}")
            preferences[PreferencesKeys.REPORT_TYPE] = reportType.name
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            Log.d(TAG, "updateSortOrder: ${sortOrder.name}")
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateSortBy(sortBy: SortBy) {
        dataStore.edit { preferences ->
            Log.d(TAG, "updateSortBy: ${sortBy.name}")
            preferences[PreferencesKeys.SORT_BY] = sortBy.name
        }
    }

    suspend fun updateRangeSliderValue(fameValueList: List<Float>) {
        dataStore.edit { preferences ->
            Log.d(TAG, "updateSliderRangeValue: ${fameValueList[0]} ${fameValueList[1]}")
            if (preferences[PreferencesKeys.SLIDER_FROM] == fameValueList[0] &&
                preferences[PreferencesKeys.SLIDER_TO] == fameValueList[1]
            ) {
                return@edit
            }
            preferences[PreferencesKeys.SLIDER_FROM] = fameValueList[0]
            preferences[PreferencesKeys.SLIDER_TO] = fameValueList[1]
            preferences[PreferencesKeys.SLIDER_MIN_STEP] = fameValueList[0]
            preferences[PreferencesKeys.SLIDER_MAX_STEP] = fameValueList[1]
            preferences[PreferencesKeys.SLIDER_STEP_SIZE] = (fameValueList[1] - fameValueList[0]) / 10
        }
    }

    suspend fun updateFameValue(fameValueList: List<Float>) {
        dataStore.edit { preferences ->
            Log.d(TAG, "updateFameValue: ${fameValueList[0]} ${fameValueList[1]}")
            preferences[PreferencesKeys.SLIDER_MIN_STEP] = fameValueList[0]
            preferences[PreferencesKeys.SLIDER_MAX_STEP] = fameValueList[1]
        }
    }

    /**
     * Update Settings
     */
    val settingsPreferences = dataStore.data
        .catch { exception ->
            Log.e(TAG, "settingsPreferences catch: ", exception)
            onCatch(exception)
        }.map { preferences ->
            val theme = preferences[PreferencesKeys.APP_THEME] ?: "follow_device_theme"
            val sendReportData = preferences[PreferencesKeys.SEND_REPORT_DATA] ?: false

            return@map SettingsPreferences(
                theme, sendReportData
            )
        }

    suspend fun updateTrackDeviceTheme(value: String) {
        Log.d(TAG, "updateTrackDeviceTheme: $value")
        dataStore.edit {
            it[PreferencesKeys.APP_THEME] = value
        }
    }

    suspend fun updateSendReportData(boolean: Boolean) {
        Log.d(TAG, "updateSendReportData: $boolean")
        dataStore.edit {
            it[PreferencesKeys.SEND_REPORT_DATA] = boolean
        }
    }

    private suspend fun FlowCollector<Preferences>.onCatch(exception: Throwable) {
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }

    private object PreferencesKeys {
        // Filter keys
        val REPORT_TYPE = preferencesKey<String>("report_type")
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val SORT_BY = preferencesKey<String>("sort_by")
        val SLIDER_FROM = preferencesKey<Float>("slider_from")
        val SLIDER_TO = preferencesKey<Float>("slider_to")
        val SLIDER_MIN_STEP = preferencesKey<Float>("slider_min")
        val SLIDER_MAX_STEP = preferencesKey<Float>("slider_max")
        val SLIDER_STEP_SIZE = preferencesKey<Float>("slider_step_size")

        // Settings keys
        val APP_THEME = preferencesKey<String>("app_theme")
        val SEND_REPORT_DATA = preferencesKey<Boolean>("send_report_data")
    }
}