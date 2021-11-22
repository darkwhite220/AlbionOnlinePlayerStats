package earth.darkwhite.albiononlineplayerstats.database.dataconverter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import earth.darkwhite.albiononlineplayerstats.data.api.model.KDData

class KDDataConverter {

    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(KDData::class.java)

    @TypeConverter
    fun fromStringToList(value: String): KDData? {
        if (value.isEmpty()) return null

        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromListToString(value: KDData): String? {

        return adapter.toJson(value)
    }
}