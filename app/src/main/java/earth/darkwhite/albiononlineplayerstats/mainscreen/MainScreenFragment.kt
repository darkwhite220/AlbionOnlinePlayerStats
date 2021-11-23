package earth.darkwhite.albiononlineplayerstats.mainscreen

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import earth.darkwhite.albiononlineplayerstats.*
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.databinding.MainScreenFragmentBinding
import earth.darkwhite.albiononlineplayerstats.mainscreen.DropDownAdapter.DropDownClickListener
import earth.darkwhite.albiononlineplayerstats.mainscreen.MainScreenAdapter.MainScreenAdapterListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.NumberFormat
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainScreenFragment : Fragment(), MainScreenAdapterListener, DropDownClickListener {

    private val viewModelMainScreen: MainScreenViewModel by viewModels()
    private lateinit var binding: MainScreenFragmentBinding
    private val adapterDropDown = DropDownAdapter(this)
    private val adapterMainList = MainScreenAdapter(this)

    companion object {
        const val TAG = "MainScreenFragment"
        const val ANIMATION_DURATION = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(/* growing= */ false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(/* growing= */ true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")

        postponeEnterTransition()

        binding = DataBindingUtil.inflate(inflater, R.layout.main_screen_fragment, container, false)

        binding.apply {
            // For layout enter animation
            root.doOnPreDraw { startPostponedEnterTransition() }
            // Main layout
            viewModel = viewModelMainScreen
            lifecycleOwner = viewLifecycleOwner
        }

        bindingReportsRecyclerView()

        bindingPlayersRecyclerView()

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.constraintRvParent.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_top_corners)
        }

        // Remove toolbar shadow
        (activity as AppCompatActivity?)!!.supportActionBar?.elevation = 0f

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        // Override onBackPressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressed)

        // Navigate to AddPlayerFragment
        navigateToAddPlayerFrag()

        // Fame slider change
        fameSliderChange()

        viewModelMainScreen.apply {
            trackedUsers.observeForever(observeForeverTrackedUsers)
            fameRangeSliderValues.observeForever(observeForeverTrackedUsersData)
            displayFailLoadingSnackBar.observe(viewLifecycleOwner, { value ->
                Log.d(TAG, "displayFailLoadingSnackBar: $value")
                if (value != 0) {
                    displaySnackBar(binding.root, getString(R.string.fetch_user_data_failed, value))
                    viewModelMainScreen.restFailCounter()
                }
            })
        }
    }

    private fun bindingReportsRecyclerView() {
        binding.apply {
            viewModel = viewModelMainScreen
            lifecycleOwner = viewLifecycleOwner
            rvMainReportsList.apply {
                adapter = adapterMainList
                addOnScrollListener(rvMainReportsScrollListener())
            }
            imgElevateMainLayout.setOnClickListener(layoutAnimation(requireActivity(), binding))
        }
    }

    private fun bindingPlayersRecyclerView() {
        binding.includeDropDown.apply {
            viewModel = viewModelMainScreen
            lifecycleOwner = viewLifecycleOwner
            recyclerViewPlayers.adapter = adapterDropDown
            chipGroupReportsType.onReportsTypeChangeListener(viewModelMainScreen)
            chipGroupSortBy.onSortByChangeListener(viewModelMainScreen, sliderFameRange)
            chipGroupSortOrder.onSortOrderChangeListener(viewModelMainScreen)
            onStartSetFilterChipCheck(viewLifecycleOwner, viewModelMainScreen)
        }
    }

    private fun fameSliderChange() {
        binding.includeDropDown.sliderFameRange.apply {
            setLabelFormatter { NumberFormat.getInstance().format(it.roundToInt()).toString() }
            addOnSliderTouchListener(onSliderTouchListener(viewModelMainScreen))
            onStartSetRangeSliderValues(viewLifecycleOwner, viewModelMainScreen)
        }
    }

    // Only run when new player added
    private val observeForeverTrackedUsers = Observer<List<Player>?> {
        Log.d(TAG, "observeForeverTrackedUsers: trackedUsers.observeForever")
        viewModelMainScreen.fetchNewData()
    }

    private val observeForeverTrackedUsersData = Observer<List<Float>?> {
        Log.d(TAG, "observeForeverTrackedUsersData: ")
    }

    private fun navigateToAddPlayerFrag() {
        binding.fabAddPlayer.apply {
            setOnClickListener {
                // Map the start View in FragmentA and the transitionName of the end View in FragmentB
                val extras = FragmentNavigatorExtras((it to getString(R.string.transition_name)) as Pair<View, String>)
                this.findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToAddPlayerFragment(), extras)
            }
        }
    }

    /**
     * MainReports RecyclerView click listener
     */
    override fun onPvpDataClick(constrainLayout: View, pvpData: ReportsResponse) {
        Log.d(TAG, "onPvpDataClick: ${pvpData.victimData.name}")
        val detailFragmentTransitionName = getString(R.string.detail_fragment_transition_name)
        val extras = FragmentNavigatorExtras(constrainLayout to detailFragmentTransitionName)
        this.findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToDetailFragment(pvpData), extras)
    }

    /**
     * Dropdown RecyclerView click listener
     */
    override fun onChipClick(chip: View, player: Player) {
        val chipView = (chip as Chip)
        chipView.isChecked != chipView.isChecked
        Log.d(TAG, "onChipClick: ${player.name} ${chipView.isChecked}")

        // Modify checked state and update DB
        viewModelMainScreen.trackedUsers.value?.find {
            it.name == chipView.text
        }?.run {
            this.selected = chipView.isChecked
            viewModelMainScreen.updatePlayer(this)
        }
    }

    override fun onDeleteClick(frameLayout: View, player: Player) {
        Log.d(TAG, "onDeleteClick: ${player.name}")
        viewModelMainScreen.deletePlayerAndHisReports(playerName = player.name)
        displaySnackBar(binding.root, getString(R.string.player_deleted, player.name))
    }

    /**
     * Reports recyclerView scroll listener
     * Hide/Show fab
     */
    private fun rvMainReportsScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && binding.fabAddPlayer.isShown) {
                binding.fabAddPlayer.hide()
            } else if (dy < 0 && !binding.fabAddPlayer.isShown) {
                binding.fabAddPlayer.show()
            }
        }
    }

    /**
     * Reports recyclerView adapter observer
     * Scroll up on every change
     */
    private val adapterMainListObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
//            Log.d(TAG, "onChanged: ")
            binding.rvMainReportsList.scrollToPosition(0)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
//            Log.d(TAG, "onItemRangeChanged: ")
            binding.rvMainReportsList.scrollToPosition(0)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
//            Log.d(TAG, "onItemRangeInserted: ")
            binding.rvMainReportsList.scrollToPosition(0)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
//            Log.d(TAG, "onItemRangeRemoved: ")
            binding.rvMainReportsList.scrollToPosition(0)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
//            Log.d(TAG, "onItemRangeMoved: ")
            binding.rvMainReportsList.scrollToPosition(0)
        }
    }

    // OnBackPressed, if dropDown layout is visible close it
    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.d(TAG, "handleOnBackPressed: ")
            isEnabled = !isEnabled
            if (backdropShown) {
                animateFilterLayout(requireActivity(), binding)
            } else {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_reload -> {
                if (viewModelMainScreen.loadingProgress.value == false) {
                    viewModelMainScreen.startRequestForNewReportsData()
                } else {
                    displaySnackBar(this.binding.root, getString(R.string.already_loading_data))
                }
                true
            }
            R.id.menu_filter -> {
                animateFilterLayout(requireActivity(), binding)
                true
            }
            else -> NavigationUI.onNavDestinationSelected(item, this.findNavController()) || super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        adapterMainList.registerAdapterDataObserver(adapterMainListObserver)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        backdropShown = false // reset
        adapterMainList.unregisterAdapterDataObserver(adapterMainListObserver)
    }

}


