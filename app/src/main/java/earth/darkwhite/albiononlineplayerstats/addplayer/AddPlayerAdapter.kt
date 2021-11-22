package earth.darkwhite.albiononlineplayerstats.addplayer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import earth.darkwhite.albiononlineplayerstats.databinding.ItemSearchPlayerBinding
import earth.darkwhite.albiononlineplayerstats.data.api.model.PlayerData

class AddPlayerAdapter(private val listener: AddPlayerListener) : RecyclerView.Adapter<AddPlayerAdapter.MiniDataViewHolder>() {

    var data = listOf<PlayerData>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    class MiniDataViewHolder private constructor(private val binding: ItemSearchPlayerBinding, private val listener: AddPlayerListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlayerData) {
            binding.apply {
                playerData = item
                clickListener = listener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup, listener: AddPlayerListener): MiniDataViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchPlayerBinding.inflate(layoutInflater, parent, false)
                return MiniDataViewHolder(binding, listener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniDataViewHolder {
        return MiniDataViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: MiniDataViewHolder, position: Int) {
        holder.bind(data[position])
    }

    interface AddPlayerListener {
        fun onAddPlayer(view: View, playerData: PlayerData)
    }
}