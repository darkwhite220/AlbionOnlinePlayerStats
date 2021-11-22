package earth.darkwhite.albiononlineplayerstats

import android.annotation.SuppressLint
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import earth.darkwhite.albiononlineplayerstats.addplayer.AddPlayerAdapter
import earth.darkwhite.albiononlineplayerstats.data.api.model.Equipment
import earth.darkwhite.albiononlineplayerstats.data.api.model.EquipmentData
import earth.darkwhite.albiononlineplayerstats.data.api.model.PlayerData
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.detail.DetailLootAdapter
import earth.darkwhite.albiononlineplayerstats.mainscreen.DropDownAdapter
import earth.darkwhite.albiononlineplayerstats.mainscreen.MainScreenAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat

private const val TAG = "BindingAdapter"

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<ReportsResponse>?) {
    Log.d(TAG, "listData bindRecyclerView: ")
    val adapter = recyclerView.adapter as MainScreenAdapter
    recyclerView.setHasFixedSize(true)
    adapter.submitList(data)
}

@BindingAdapter("imageKillDeath")
fun bindImgKillDeath(imageView: ImageView, data: Boolean) {
    if (data) {
        imageView.setImageResource(R.drawable.ic_sword)
    } else {
        imageView.setImageResource(R.drawable.ic_skull)
    }
}

@BindingAdapter("userNameLeft")
fun bindUsernameL(textView: TextView, data: Boolean) {
    if (!data) {
        textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    } else {
        textView.paintFlags = 0
    }
}

@BindingAdapter("userNameRight")
fun bindUsernameR(textView: TextView, data: Boolean) {
    if (data) {
        textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    } else {
        textView.paintFlags = 0
    }
}

@BindingAdapter("allianceNameLeft")
fun bindAllianceNameL(textView: TextView, data: ReportsResponse) {
    val sb = StringBuilder()
    if (data.victimData.allianceTag?.isNotEmpty() == true) {
        sb.apply {
            append('[')
            append(data.victimData.allianceTag)
            append("] ")
        }
    }
    if (data.victimData.guildName?.isNotEmpty() == true) {
        sb.append(data.victimData.guildName)
    } else {
        sb.append('-')
    }
    textView.text = sb.toString()
}

@BindingAdapter("allianceNameRight")
fun bindAllianceNameR(textView: TextView, data: ReportsResponse) {
    val sb = StringBuilder()
    if (data.killerData?.allianceTag?.isNotEmpty() == true) {
        sb.apply {
            append('[')
            append(data.killerData.allianceTag)
            append("] ")
        }
    }
    if (data.killerData?.guildName?.isNotEmpty() == true) {
        sb.append(data.killerData.guildName)
    } else {
        sb.append('-')
    }
    textView.text = sb.toString()
}

@BindingAdapter("itemPower")
fun bindItemPower(textView: TextView, data: Double) {
    textView.text = data.toInt().toString()
}

@BindingAdapter("fame")
fun bindFame(textView: TextView, value: Long) {
    val fame = value.toFloat()
    textView.text = when {
        fame >= 1000000000 -> {
            "${NumberFormat.getInstance().format((fame / 1000000000))} B"
        }
        fame >= 1000000 -> {
            "${NumberFormat.getInstance().format((fame / 1000000))} M"
        }
        fame >= 1000 -> {
            "${NumberFormat.getInstance().format((fame / 1000).toInt())} K"
        }
        else -> {
            NumberFormat.getInstance().format(fame.toInt())
        }
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("time")
fun bindTvTime(textView: TextView, data: String) {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat.getDateTimeInstance()
    try {
        textView.text = formatter.format(parser.parse(data)!!)
    } catch (e: java.text.ParseException) {
        textView.text = textView.resources.getString(R.string.error_parsing_date)
    }
}

@BindingAdapter("versus")
fun bindTvVersusR(textView: TextView, data: ReportsResponse) {
    textView.text = "${data.numberOfParticipants}"
}

@BindingAdapter("loot")
fun bindTvLoot(textView: TextView, data: List<EquipmentData?>?) {
    var str = "0"
    data?.apply {
        val list = mutableListOf<EquipmentData?>()
        data.forEach {
            if (it != null) {
                list.add(it)
            }
        }
        str = "${list.size}"
    }
    textView.text = str
}

@BindingAdapter("lootEmpty")
fun bindTvLootEmpty(textView: TextView, data: List<EquipmentData?>?) {
    textView.visibility = View.VISIBLE
    data?.apply {
        data.forEach {
            if (it != null) {
                textView.visibility = View.GONE
                return@apply
            }
        }
    }
}

@BindingAdapter("lootTitle")
fun bindTvLootTitle(textView: TextView, data: String) {
    val str = "$data's inventory"
    textView.text = str
}

/**
 * Drop down layout
 */
@BindingAdapter("listDataPlayers")
fun bindPlayersRecyclerView(recyclerView: RecyclerView, list: List<Player>?) {
    if (list != null) {
        Log.d(TAG, "Drop down layout bindPlayersRecyclerView: ${list.size}")
        val adapter = recyclerView.adapter as DropDownAdapter?
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = when (list.size) {
            in 0..3 -> LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
            in 4..6 -> GridLayoutManager(recyclerView.context, 3, RecyclerView.HORIZONTAL, false)
            else -> StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        }
        adapter?.data = list
    }
}

/**
 * Item_Equipment
 */
@BindingAdapter("createImageUrl")
fun bindImageAndStartGlide(imageView: ImageView, data: EquipmentData?) {
    data?.apply {
        loadImage(imageView, data.itemId)
    }
}

@BindingAdapter("createImageUrlOffHand")
fun bindImageAndStartGlide(imageView: ImageView, data: Equipment?) {
    data?.apply {
        if (data.offHand != null) {
            loadImage(imageView, data.offHand.itemId)
        } else {
            if (data.mainHand?.type?.contains("2H") == true) {
                imageView.alpha = 0.4f
                loadImage(imageView, data.mainHand.itemId)
            }
        }
    }
}

@BindingAdapter("tvQuantityVisibility")
fun bindTvQuantity(textView: TextView, data: Int) {
    when (data) {
        0 -> textView.visibility = View.GONE
        else -> textView.visibility = View.VISIBLE
    }
}

/**
 * Detail Fragment
 */

@BindingAdapter("listDataEquipment")
fun bindRecyclerViewEquipment(recyclerView: RecyclerView, data: List<EquipmentData?>?) {
    val adapter = recyclerView.adapter as DetailLootAdapter
    data?.apply {
        val list = mutableListOf<EquipmentData?>()
        data.forEach {
            if (it != null) {
                list.add(it)
            }
        }

        if (list.size != 0) {
//        list.sortByDescending { it?.type }
            recyclerView.setHasFixedSize(true)
            recyclerView.visibility = View.VISIBLE
            val context = recyclerView.context
            recyclerView.layoutManager = GridLayoutManager(context, context.resources.getInteger(R.integer.span_count))
            adapter.data = list
        } else {
            recyclerView.visibility = View.GONE
        }
    }
}

/**
 * AddPlayer Layout
 */

@BindingAdapter("addPlayerRv")
fun bindAddPlayerRV(recyclerView: RecyclerView, list: List<PlayerData>?) {
    Log.d(TAG, "bindAddPlayerRV: ")
    val adapter = recyclerView.adapter as AddPlayerAdapter
    recyclerView.setHasFixedSize(true)
    adapter.data = list ?: listOf()
}

@BindingAdapter("guildAlliance")
fun bindGuildAlliance(textView: TextView, data: PlayerData) {
    Log.d("BindingAdapterAddPlayer", "BindGuildAlliance: ")
    val sb = StringBuilder()
    if (data.allianceName?.isNotEmpty() == true) {
        sb.apply {
            append('[')
            append(data.allianceName)
            append("] ")
        }
    }
    if (data.guildName?.isNotEmpty() == true) {
        sb.append(data.guildName)
    } else {
        sb.append('-')
    }
    textView.text = sb.toString()
}