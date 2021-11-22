package earth.darkwhite.albiononlineplayerstats.database.dataconverter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import earth.darkwhite.albiononlineplayerstats.database.domain.PvPData

class SoloKDDataConverter {

    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(PvPData.KDData::class.java)

    @TypeConverter
    fun fromStringToList(value: String): PvPData.KDData? {
        if (value.isEmpty()) return null

        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromListToString(value: PvPData.KDData): String? {

        return adapter.toJson(value)
    }
}