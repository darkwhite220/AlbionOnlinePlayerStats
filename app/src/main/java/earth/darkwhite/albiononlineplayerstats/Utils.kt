package earth.darkwhite.albiononlineplayerstats

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import earth.darkwhite.albiononlineplayerstats.addplayer.AddPlayerViewModel

/**
 * Used to display fetch data request status to the user
 */
enum class RequestStatus { LOADING, DONE }

/**
 * Base Url to render item images
 */
private const val RENDER_BASE_URL = "https://render.albiononline.com/v1/item/"

/**
 * Create item image url
 */
fun imageUrl(query: String): String = RENDER_BASE_URL + query

/**
 * ProgressBar
 */
fun progressBar(context: Context): CircularProgressDrawable {
    val progress = CircularProgressDrawable(context)
    progress.apply {
        strokeWidth = 2f
        centerRadius = 20f
        start()
    }
    return progress
}

/**
 * Glide
 */
fun loadImage(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(imageUrl(url))
        .placeholder(progressBar(imageView.context))
        .skipMemoryCache(true)
        .error(R.drawable.ic_error)
        .into(imageView)
}

/**
 * Takes String to display in a SnackBar
 */
fun displaySnackBar(view: View, string: String) {
    Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()
}

/**
 * AddPlayer Fragment
 */

fun getQuery(editText: EditText, context: Context, viewModel: AddPlayerViewModel) {
    if (viewModel.status.value != RequestStatus.LOADING) {
        editText.text?.apply {
            if ((this.isNotEmpty() || this.isNotBlank()) && this.length > 3) {
                Log.d(earth.darkwhite.albiononlineplayerstats.mainscreen.TAG, "onViewCreated: start loading")
//                viewModel.initFirstDataSearch(this.toString())
                viewModel.initFetchData(this.toString())
                // Hide keyboard
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }
}
