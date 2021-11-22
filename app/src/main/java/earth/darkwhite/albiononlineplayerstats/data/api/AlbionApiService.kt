package earth.darkwhite.albiononlineplayerstats.data.api

import earth.darkwhite.albiononlineplayerstats.data.api.model.PlayerResponse
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://gameinfo.albiononline.com/api/gameinfo/"

interface ApiService {
    @GET("search")
    suspend fun initSearch(@Query("q") value: String): PlayerResponse

    @GET("players/{userId}/deaths")
    suspend fun getPlayerDeaths(@Path("userId") userId: String): List<ReportsResponse>

    @GET("players/{userId}/kills")
    suspend fun getPlayerKills(@Path("userId") userId: String): List<ReportsResponse>
}
