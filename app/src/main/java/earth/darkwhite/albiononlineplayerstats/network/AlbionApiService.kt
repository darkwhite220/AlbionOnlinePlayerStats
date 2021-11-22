package earth.darkwhite.albiononlineplayerstats.network

import earth.darkwhite.albiononlineplayerstats.database.domain.PvPData
import earth.darkwhite.albiononlineplayerstats.domain.MiniData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://gameinfo.albiononline.com/api/gameinfo/"

interface ApiService {
    @GET("search")
    suspend fun initSearch(@Query("q") value: String): MiniData

    @GET("players/{userId}/deaths")
    suspend fun getPlayerDeaths(@Path("userId") userId: String): List<PvPData>

    @GET("players/{userId}/kills")
    suspend fun getPlayerKills(@Path("userId") userId: String): List<PvPData>
}
