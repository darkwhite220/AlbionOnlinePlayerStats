package earth.darkwhite.albiononlineplayerstats.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_players")
data class Player(
    @PrimaryKey
    val id: String,
    val name: String,
    var selected: Boolean = true
)
