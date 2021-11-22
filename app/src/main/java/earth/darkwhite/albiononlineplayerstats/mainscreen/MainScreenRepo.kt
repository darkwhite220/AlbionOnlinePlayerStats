package earth.darkwhite.albiononlineplayerstats.mainscreen

import androidx.lifecycle.asLiveData
import earth.darkwhite.albiononlineplayerstats.FilterPreferences
import earth.darkwhite.albiononlineplayerstats.data.api.ApiService
import earth.darkwhite.albiononlineplayerstats.data.api.mappers.toReportContainer
import earth.darkwhite.albiononlineplayerstats.data.api.mappers.toReportsDataIn
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.database.MyDao
import earth.darkwhite.albiononlineplayerstats.database.mappers.ReportsDataOut
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.database.model.ReportContainer
import earth.darkwhite.albiononlineplayerstats.database.model.ReportDataIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

class MainScreenRepo @Inject constructor(
    private val dataSource: MyDao,
    private val api: ApiService
) {

    val trackedUsers = dataSource.getPlayers().asLiveData()

    val trackedUsersData = dataSource.getPvPData().asLiveData()

    // Reports query
    @ExperimentalCoroutinesApi
    fun getPvPDataQuery(
        query: MutableList<String>,
        preferences: FilterPreferences
    ): Flow<List<ReportsResponse>> {
        return dataSource.getPvPDataQuery(
            query,
            preferences.minStep.toLong(),
            preferences.maxStep.toLong(),
            preferences.reportType,
            preferences.sortBy,
            preferences.sortOrder
        )
            .transformLatest { value: List<ReportsDataOut> ->
                emit(transformReportsDataToPvPData(value))
            }
    }

    suspend fun insertPlayer(player: Player) = dataSource.insertPlayer(player)

    suspend fun updatePlayer(player: Player) = dataSource.updatePlayer(player)

    // Insert reports
    suspend fun kills(id: String): List<ReportsResponse> {
        val temp = latestPlayerKillData(id)
        temp.forEach { it.isTheKiller = true }
        insertPlayerData(temp)
        return temp
    }

    // Insert reports
    suspend fun deaths(id: String): List<ReportsResponse> {
        val temp = latestPlayerDeathData(id)
        insertPlayerData(temp)
        return temp
    }

    private suspend fun latestPlayerKillData(id: String): List<ReportsResponse> = api.getPlayerKills(id)

    private suspend fun latestPlayerDeathData(id: String): List<ReportsResponse> = api.getPlayerDeaths(id)

    private suspend fun insertPlayerData(playerData: List<ReportsResponse>) {
        val pvPDataList = mutableListOf<ReportContainer>()
        val kdDataList = mutableListOf<ReportDataIn>()
        playerData.forEach { report ->
            pvPDataList.add(report.toReportContainer(report.killerData?.name, report.victimData.name))
            kdDataList.add(
                ReportDataIn(
                    report.eventId,
                    report.killerData?.toReportsDataIn(),
                    report.victimData.toReportsDataIn()
                )
            )
        }
        dataSource.insertReportContainer(pvPDataList)
        dataSource.insertKDDataContainer(kdDataList)
    }


    // Delete player with his reports data
    suspend fun deletePlayerAndHisReports(playerName: String) {
        dataSource.deletePlayer(playerName)
        dataSource.deletePlayerReportsData(playerName)
        dataSource.deletePlayerDataContainer(playerName)
    }

}