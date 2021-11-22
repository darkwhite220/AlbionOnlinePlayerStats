package earth.darkwhite.albiononlineplayerstats.domain

import com.squareup.moshi.Json

data class Player(
    @Json(name = "Id")
    val id: String,
    @Json(name = "Name")
    val name: String,
    @Json(name = "GuildId")
    val guildId: String,
    @Json(name = "GuildName")
    val guildName: String,
    @Json(name = "AllianceId")
    val allianceId: String,
    @Json(name = "AllianceName")
    val allianceName: String,
    @Json(name = "AllianceTag")
    val allianceTag: String,
    @Json(name = "Avatar")
    val avatar: String,
    @Json(name = "AvatarRing")
    val avatarRing: String,
    @Json(name = "KillFame")
    val killFame: Long,
    @Json(name = "DeathFame")
    val deathFame: Long,
    @Json(name = "FameRatio")
    val fameRation: Long,
    @Json(name = "LifetimeStatistics")
    val playerStats: PlayerStats
)

data class PlayerStats(
    val crystalLeague: Long,
    val fishingFame: Long,
    val farmingFame: Long
)
