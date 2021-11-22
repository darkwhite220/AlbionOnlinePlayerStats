package earth.darkwhite.albiononlineplayerstats.database.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_players")
data class Player(
    @PrimaryKey
    val id: String,
    val name: String
)
