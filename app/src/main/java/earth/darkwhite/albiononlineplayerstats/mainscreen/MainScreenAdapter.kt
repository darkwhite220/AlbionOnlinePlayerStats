package earth.darkwhite.albiononlineplayerstats.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import earth.darkwhite.albiononlineplayerstats.databinding.ItemMainScreenBinding
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.mainscreen.MainScreenAdapter.MainScreenViewHolder

class MainScreenAdapter(private val listener: MainScreenAdapterListener) :
    ListAdapter<ReportsResponse, MainScreenViewHolder>(DiffCallback()) {

    class MainScreenViewHolder private constructor(private var binding: ItemMainScreenBinding, private val dataListener: MainScreenAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReportsResponse) {
            binding.apply {
                pvpData = item
                listener = dataListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup, listener: MainScreenAdapterListener): MainScreenViewHolder {
                return MainScreenViewHolder(
                    ItemMainScreenBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    listener
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainScreenViewHolder {
        return MainScreenViewHolder.from(
            parent,
            listener
        )
    }

    override fun onBindViewHolder(holder: MainScreenViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface MainScreenAdapterListener {
        fun onPvpDataClick(constrainLayout: View, pvpData: ReportsResponse)
    }
}

class DiffCallback : DiffUtil.ItemCallback<ReportsResponse>() {
    override fun areItemsTheSame(oldItem: ReportsResponse, newItem: ReportsResponse): Boolean {
        return oldItem.eventId == newItem.eventId
    }

    override fun areContentsTheSame(oldItem: ReportsResponse, newItem: ReportsResponse): Boolean {
        return oldItem == newItem
    }
}