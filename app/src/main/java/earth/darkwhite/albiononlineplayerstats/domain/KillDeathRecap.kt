package earth.darkwhite.albiononlineplayerstats.domain

import com.squareup.moshi.Json

data class KillDeathRecap(
    val numberOfParticipants: Int,
    val groupMemberCount: Int,
    @Json(name = "EventId")
    val eventId: Long,
    @Json(name= "TimeStamp")
    val time: String,
    @Json(name = "Version")
    val version: Int,
    @Json(name = "TotalVictimKillFame")
    val fame: Long,
//    @Json(name = "Location")
//    val location: String,
//    @Json(name = "GvGMatch")
//    val gvgMatch: Boolean,
    @Json(name = "KillArea")
    val killArea: String

    )
