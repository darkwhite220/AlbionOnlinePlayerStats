package earth.darkwhite.albiononlineplayerstats.domain

import com.squareup.moshi.Json

data class Guild(
    @Json(name = "Id")
    val id: String,
    @Json(name = "Name")
    val name: String,
    @Json(name = "FounderId")
    val founderId: String,
    @Json(name = "FounderName")
    val founderName: String,
    @Json(name = "Founded")
    val founded: String,
    @Json(name = "AllianceTag")
    val allianceTag: String,
    @Json(name = "AllianceId")
    val allianceId: String,
    @Json(name = "AllianceName")
    val allianceName: String,
    @Json(name = "killFame")
    val killFame: Long,
    @Json(name = "DeathFame")
    val deathFame: Long,
    @Json(name = "MemberCount")
    val memberCount: Int,
)
