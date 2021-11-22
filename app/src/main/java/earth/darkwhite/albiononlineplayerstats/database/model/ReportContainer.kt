package earth.darkwhite.albiononlineplayerstats.database.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse

@JsonClass(generateAdapter = true)
@Entity(tableName = "tracked_players_data")
@kotlinx.parcelize.Parcelize
data class ReportContainer(
    @PrimaryKey
    @Json(name = "EventId")
    val eventId: Long,
    var attacker: String? = "",
    var victim: String? = "",
    val numberOfParticipants: Int,
    val groupMemberCount: Int,
    @Json(name = "TimeStamp")
    val time: String,
    @Json(name = "Version")
    val version: Int,
    @Json(name = "TotalVictimKillFame")
    val fame: Long,
    @Json(name = "Location")
    val location: String?,
    @Json(name = "GvGMatch")
    val gvgMatch: Boolean?,
    @Json(name = "KillArea")
    val killArea: String?,
    var isTheKiller: Boolean = false
) : Parcelable





