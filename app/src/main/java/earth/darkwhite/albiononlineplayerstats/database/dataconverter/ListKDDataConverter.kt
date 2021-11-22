package earth.darkwhite.albiononlineplayerstats.database.dataconverter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import earth.darkwhite.albiononlineplayerstats.database.domain.PvPData

class ListKDDataConverter {

    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(List::class.java, PvPData.KDData::class.java)
    private val adapter = moshi.adapter<List<PvPData.KDData>>(type)

    @TypeConverter
    fun fromStringToList(value: String): List<PvPData.KDData>? {
        if (value.isEmpty()) return null

        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromListToString(value: List<PvPData.KDData>): String? {
        if (value.isEmpty()) return null

        return adapter.toJson(value)
    }
}