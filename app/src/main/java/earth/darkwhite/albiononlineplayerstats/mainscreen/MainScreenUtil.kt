package earth.darkwhite.albiononlineplayerstats.mainscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Interpolator
import androidx.fragment.app.FragmentActivity
import androidx.transition.Fade
import androidx.transition.TransitionManager
import earth.darkwhite.albiononlineplayerstats.R
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.database.mappers.ReportsDataOut
import earth.darkwhite.albiononlineplayerstats.database.mappers.toReportsResponse
import earth.darkwhite.albiononlineplayerstats.databinding.MainScreenFragmentBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.roundToInt

//private const val TAG = "Utils"

// Animation
private val interpolator: Interpolator? = null
private val animatorSet = AnimatorSet()
private val displayMetrics = DisplayMetrics()
var height: Int = 0
var backdropShown = false

/**
 * Dropdown animation
 */
@ExperimentalCoroutinesApi
fun animateFilterLayout(fragActivity: FragmentActivity, binding: MainScreenFragmentBinding) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val display = fragActivity.display
        display?.getRealMetrics(displayMetrics)
    } else {
        @Suppress("DEPRECATION")
        val display = (fragActivity as Activity).windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(displayMetrics)
    }

    height = displayMetrics.heightPixels

    backdropShown = !backdropShown

    animateUpArrow(binding)

    // Fab visibility
    if (backdropShown) binding.fabAddPlayer.hide() else binding.fabAddPlayer.show()

    // Cancel the existing animations
    animatorSet.removeAllListeners()
    animatorSet.end()
    animatorSet.cancel()

    val sizeOfSoftKeyButtonBar = dpToPx(getSoftNavigationBarSize(fragActivity.resources), fragActivity)
    val translateY = height - (fragActivity.resources.getDimensionPixelSize(R.dimen._70dp) + sizeOfSoftKeyButtonBar)

    val animator = ObjectAnimator.ofFloat(binding.constraintRvParent, "translationY", (if (backdropShown) translateY else 0).toFloat())
    animator.duration = MainScreenFragment.ANIMATION_DURATION
    if (interpolator != null) {
        animator.interpolator = interpolator
    }
    animatorSet.play(animator)
    animator.start()
}

/**
 * Display when main layout is at the bottom
 */
@ExperimentalCoroutinesApi
private fun animateUpArrow(binding: MainScreenFragmentBinding) {
    val transition = Fade()
    transition.apply {
        duration = MainScreenFragment.ANIMATION_DURATION
        addTarget(binding.imgElevateMainLayout)
    }
    TransitionManager.beginDelayedTransition(binding.constraintRvParent, transition)
    binding.imgElevateMainLayout.visibility = if (backdropShown) View.VISIBLE else View.INVISIBLE
}

// Convert int to Pixel density
private fun dpToPx(dp: Int, fragActivity: FragmentActivity): Int {
    val density: Float = fragActivity.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}

// getSoftNavigationBarSize
private fun getSoftNavigationBarSize(resources: Resources): Int {
    var result = 0
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * Repository Utils
 */
/**
 * Transform ReportsData To PvPData
 */
fun transformReportsDataToPvPData(list: List<ReportsDataOut>): List<ReportsResponse> {
    val newList = mutableListOf<ReportsResponse>()
    list.onEach {
        newList.add(it.toReportsResponse())
    }
    return newList
}