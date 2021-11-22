package earth.darkwhite.albiononlineplayerstats

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputLayout
import earth.darkwhite.albiononlineplayerstats.addplayer.AddPlayerViewModel
import earth.darkwhite.albiononlineplayerstats.databinding.DropDownLayoutBinding
import earth.darkwhite.albiononlineplayerstats.databinding.MainScreenFragmentBinding
import earth.darkwhite.albiononlineplayerstats.mainscreen.MainScreenFragment
import earth.darkwhite.albiononlineplayerstats.mainscreen.MainScreenViewModel
import earth.darkwhite.albiononlineplayerstats.mainscreen.TAG
import earth.darkwhite.albiononlineplayerstats.mainscreen.animateFilterLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * MainScreen
 */

/**
 * DropDown layout
 */
// Filter ChipGroup listener ReportsType
@ExperimentalCoroutinesApi
fun ChipGroup.onReportsTypeChangeListener(viewModel: MainScreenViewModel) {
    Log.d(TAG, "onReportsTypeChangeListener: ")
    this.setOnCheckedChangeListener { _, checkedId ->
        when (checkedId) {
            R.id.chip_deaths_kills -> viewModel.onReportTypeChange(ReportType.BOTH)
            R.id.chip_deaths -> viewModel.onReportTypeChange(ReportType.DEATHS)
            R.id.chip_kills -> viewModel.onReportTypeChange(ReportType.KILLS)
        }
    }
}

// Filter ChipGroup listener SortBy
@ExperimentalCoroutinesApi
fun ChipGroup.onSortByChangeListener(viewModel: MainScreenViewModel, rangeSlider: RangeSlider) {
    Log.d(TAG, "onSortByChangeListener: ")
    this.setOnCheckedChangeListener { _, checkedId ->
        when (checkedId) {
            R.id.chip_time -> {
                viewModel.onSortByChange(SortBy.DATE)
                rangeSlider.visibility = View.GONE
            }
            R.id.chip_fame -> {
                viewModel.onSortByChange(SortBy.FAME)
                rangeSlider.visibility = View.VISIBLE
            }
        }
    }
}

// Filter ChipGroup listener SortOrder
@ExperimentalCoroutinesApi
fun ChipGroup.onSortOrderChangeListener(viewModel: MainScreenViewModel) {
    Log.d(TAG, "onSortOrderChangeListener: ")
    this.setOnCheckedChangeListener { _, checkedId ->
        when (checkedId) {
            R.id.chip_asc -> viewModel.onSortOrderChange(SortOrder.ASC)
            R.id.chip_desc -> viewModel.onSortOrderChange(SortOrder.DESC)
        }
    }
}

// Filter Touch listener SliderTouch
@ExperimentalCoroutinesApi
fun RangeSlider.onSliderTouchListener(viewModel: MainScreenViewModel) = object : RangeSlider.OnSliderTouchListener {
    override fun onStartTrackingTouch(slider: RangeSlider) {
        Log.d(MainScreenFragment.TAG, "onStartTrackingTouch: ")
    }

    override fun onStopTrackingTouch(slider: RangeSlider) {
        Log.d(MainScreenFragment.TAG, "onStopTrackingTouch: ")
        viewModel.onFameValueChange(values)
    }
}

// On Start Filters checker
@ExperimentalCoroutinesApi
fun DropDownLayoutBinding.onStartSetFilterChipCheck(viewLifecycleOwner: LifecycleOwner, viewModel: MainScreenViewModel) {
    viewLifecycleOwner.lifecycleScope.launch {
        Log.d(TAG, "onStartSetFilterChipCheck: ")
        var chip = when (viewModel.filterPreferences.first().reportType) {
            ReportType.BOTH -> chipGroupReportsType[0] as Chip
            ReportType.DEATHS -> chipGroupReportsType[1] as Chip
            ReportType.KILLS -> chipGroupReportsType[2] as Chip
        }
        chip.isChecked = true
        chip = when (viewModel.filterPreferences.first().sortOrder) {
            SortOrder.ASC -> chipGroupSortOrder[0] as Chip
            SortOrder.DESC -> chipGroupSortOrder[1] as Chip
        }
        chip.isChecked = true
        chip = when (viewModel.filterPreferences.first().sortBy) {
            SortBy.DATE -> chipGroupSortBy[0] as Chip
            SortBy.FAME -> chipGroupSortBy[1] as Chip
        }
        chip.isChecked = true
    }
}

// Listen & Update RangeSlider
@ExperimentalCoroutinesApi
fun RangeSlider.onStartSetRangeSliderValues(fragment: MainScreenFragment, viewModel: MainScreenViewModel) {
    Log.d(MainScreenFragment.TAG, "onStartRangeSliderValues: ")
    fragment.activity?.lifecycleScope?.launch {
        viewModel.filterPreferences.collect { preferences ->
            Log.d(MainScreenFragment.TAG, "onStartSetRangeSliderValues: change")
            preferences.form.let { valueFrom = it }
            preferences.to.let { valueTo = it }
            values = mutableListOf(preferences.minStep, preferences.maxStep)
            this@onStartSetRangeSliderValues.stepSize = preferences.stepSize
        }
    }
}

/**
 * Animate DropDown layout Down/Up
 */
@ExperimentalCoroutinesApi
fun layoutAnimation(fragmentActivity: FragmentActivity, binding: MainScreenFragmentBinding) = View.OnClickListener {
    Log.d(TAG, "elevateMainLayout: ")
    animateFilterLayout(fragmentActivity, binding)
}

/**
 * AddPlayer Fragment
 */

fun TextInputLayout.onEndIconOnClickListener(context: Context, viewModel: AddPlayerViewModel) {
    Log.d(TAG, "onEndIconOnClickListener:")
    this.setEndIconOnClickListener {
        getQuery(this.editText!!, context, viewModel)
    }
}

fun EditText?.onEditorActionListener(context: Context, viewModel: AddPlayerViewModel) {
    Log.d(TAG, "onEditorActionListener: ")
    this?.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getQuery(this, context, viewModel)
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}
