package earth.darkwhite.albiononlineplayerstats.domain

import com.squareup.moshi.Json

data class GuildMini(
    @Json(name = "Id")
    val id: String,
    @Json(name = "Name")
    val name: String,
    @Json(name = "AllianceId")
    val allianceId: String?,
    @Json(name = "AllianceName")
    val allianceName: String?,
    @Json(name = "KillFame")
    val killFame: Long?,
    @Json(name = "DeathFame")
    val deathFame: Long?,
)
