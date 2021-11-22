package earth.darkwhite.albiononlineplayerstats.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import earth.darkwhite.albiononlineplayerstats.data.api.model.KDData

/**
 * KDData wrapper for Room DB
 * hold victim & killer data + parent "ReportContainer" eventId
 */
@JsonClass(generateAdapter = true)
@kotlinx.parcelize.Parcelize
@Entity(tableName = "report_data")
data class ReportDataIn(
    @PrimaryKey
    val eventId: Long,
    val killerData: KDData?,
    val victimData: KDData
) : Parcelable