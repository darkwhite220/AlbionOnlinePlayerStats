package earth.darkwhite.albiononlineplayerstats.addplayer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import earth.darkwhite.albiononlineplayerstats.R
import earth.darkwhite.albiononlineplayerstats.addplayer.AddPlayerAdapter.AddPlayerListener
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.databinding.AddPlayerFragmentBinding
import earth.darkwhite.albiononlineplayerstats.displaySnackBar
import earth.darkwhite.albiononlineplayerstats.data.api.model.PlayerData
import earth.darkwhite.albiononlineplayerstats.onEditorActionListener
import earth.darkwhite.albiononlineplayerstats.onEndIconOnClickListener
import kotlinx.coroutines.flow.collect

private const val TAG: String = "AddPlayerFragment"

/**
 * Display EditText for user to insert a UserName then start fetching data and display server response with similar UserName.
 * Then the user have the choice to track the desired player.
 */
@AndroidEntryPoint
class AddPlayerFragment : Fragment(), AddPlayerListener {

    private val viewModel: AddPlayerViewModel by viewModels()
    private lateinit var binding: AddPlayerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView: ")
        binding = DataBindingUtil.inflate(inflater, R.layout.add_player_fragment, container, false)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        // Fragment enter/return animations
        fragmentAnimation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        // Request focus
//        requestFocusAndDisplaySoftKeyboard()

        binding.textInputLayout.apply {
            onEndIconOnClickListener(requireContext(), viewModel)
            editText?.onEditorActionListener(requireContext(), viewModel)
        }

        binding.recyclerView.adapter = AddPlayerAdapter(this)

        selectedPlayerObserver()
    }

    /**
     * onPlayerSelected display snackBar accordingly (Already exist/Add Player)
     */
    private fun selectedPlayerObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.checkPlayer.collect { value ->
                Log.d(TAG, "checkPlayer: ")
                value?.apply {
                    val message = when (this) {
                        false -> getString(R.string.player_added_to_track_list)
                        true -> getString(R.string.player_already_in_track_list)
                    }
                    displaySnackBar(binding.root, message)
                    viewModel.resetPlayerValue()
                }
            }
        }
    }

    private fun requestFocusAndDisplaySoftKeyboard() {
        binding.textInputLayout.editText?.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun fragmentAnimation() {
        Log.d(TAG, "fragmentAnimation: init")
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab_add_player)
        enterTransition = MaterialContainerTransform().apply {
            startView = fab
            endView = binding.root
            duration = 400L
            scrimColor = Color.TRANSPARENT
            containerColor = MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.WHITE)
            startContainerColor = MaterialColors.getColor(requireContext(), R.attr.colorSecondary, Color.WHITE) // colorSecondary
            endContainerColor = MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.WHITE)
        }
        returnTransition = Slide().apply {
            duration = 400L
            addTarget(binding.root)
        }
    }

    override fun onAddPlayer(view: View, playerData: PlayerData) {
        Log.d(TAG, "onAddPlayer: ")
        viewModel.onPlayerSelected(Player(id = playerData.id, name = playerData.name))
    }

}
