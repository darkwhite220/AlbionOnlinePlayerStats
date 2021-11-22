package earth.darkwhite.albiononlineplayerstats.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import earth.darkwhite.albiononlineplayerstats.data.api.model.EquipmentData
import earth.darkwhite.albiononlineplayerstats.databinding.ItemLootBinding

class DetailLootAdapter : RecyclerView.Adapter<DetailLootAdapter.DetailLootViewHolder>() {

    var data = listOf<EquipmentData?>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class DetailLootViewHolder private constructor(private val binding: ItemLootBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: EquipmentData) {
            binding.apply {
                equipmentData = data
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): DetailLootViewHolder {
                return DetailLootViewHolder(
                    ItemLootBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailLootViewHolder {
        return DetailLootViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DetailLootViewHolder, position: Int) {
        data[position]?.let { holder.bind(it) }
    }

    override fun getItemCount() = data.size
}