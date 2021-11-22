package earth.darkwhite.albiononlineplayerstats.mainscreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.databinding.ItemPlayerChipBinding

class DropDownAdapter(private val clickListener: DropDownClickListener) : RecyclerView.Adapter<DropDownAdapter.DropDownViewHolder>() {

    var data = listOf<Player>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class DropDownViewHolder private constructor(private val binding: ItemPlayerChipBinding, private val clickListener: DropDownClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Player) {
            binding.apply {
                player = item
                listener = clickListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup, clickListener: DropDownClickListener): DropDownViewHolder {
                return DropDownViewHolder(
                    ItemPlayerChipBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    clickListener
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropDownViewHolder {
        return DropDownViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: DropDownViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    interface DropDownClickListener {
        fun onChipClick(chip: View, player: Player)
        fun onDeleteClick(frameLayout: View, player: Player)
    }
}