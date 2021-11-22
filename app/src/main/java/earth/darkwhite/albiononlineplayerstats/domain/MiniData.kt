package earth.darkwhite.albiononlineplayerstats.domain

import com.squareup.moshi.Json

data class MiniData(
    @Json(name = "guilds")
    val guildMini: List<GuildMini>?,
    @Json(name = "players")
    val playerMini: List<PlayerMini>?
)