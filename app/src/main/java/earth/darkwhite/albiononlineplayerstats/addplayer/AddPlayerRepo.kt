package earth.darkwhite.albiononlineplayerstats.addplayer

import earth.darkwhite.albiononlineplayerstats.data.api.ApiService
import earth.darkwhite.albiononlineplayerstats.database.MyDao
import earth.darkwhite.albiononlineplayerstats.database.model.Player

class AddPlayerRepo(
    private val dataSource: MyDao,
    private val api: ApiService
) {

    val getPlayers = dataSource.getPlayers()

    suspend fun fetchQuery(query: String) = api.initSearch(query).playerData

    suspend fun insertPlayer(player: Player) = dataSource.insertPlayer(player)

}