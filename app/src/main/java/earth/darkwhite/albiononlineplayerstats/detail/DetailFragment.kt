package earth.darkwhite.albiononlineplayerstats.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.*
import com.google.android.material.transition.MaterialContainerTransform
import earth.darkwhite.albiononlineplayerstats.R
import earth.darkwhite.albiononlineplayerstats.databinding.DetailFragmentBinding
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse

class DetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElemEnterAnim()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DetailFragmentBinding.inflate(inflater)

        val pvpData: ReportsResponse = DetailFragmentArgs.fromBundle(requireArguments()).data

        val viewModelFactory = DetailViewModelFactory(pvpData)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = ViewModelProvider(this@DetailFragment, viewModelFactory).get(DetailViewModel::class.java)
            recyclerViewLoot.adapter = DetailLootAdapter()
        }

        return binding.root
    }

    private fun sharedElemEnterAnim() {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment_nav_container
            resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.WHITE))
        }
    }

}