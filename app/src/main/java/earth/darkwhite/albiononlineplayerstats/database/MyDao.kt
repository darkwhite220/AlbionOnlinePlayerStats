package earth.darkwhite.albiononlineplayerstats.database

import androidx.room.*
import earth.darkwhite.albiononlineplayerstats.ReportType
import earth.darkwhite.albiononlineplayerstats.SortBy
import earth.darkwhite.albiononlineplayerstats.SortOrder
import earth.darkwhite.albiononlineplayerstats.database.mappers.ReportsDataOut
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.database.model.ReportContainer
import earth.darkwhite.albiononlineplayerstats.database.model.ReportDataIn
import kotlinx.coroutines.flow.Flow

@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@Dao
interface MyDao {

    /* Player */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun updatePlayer(player: Player)

    @Query("DELETE FROM tracked_players WHERE name LIKE :playerName")
    suspend fun deletePlayer(playerName: String)

    @Query("SELECT * FROM tracked_players ORDER BY name ASC")
    fun getPlayers(): Flow<List<Player>>

    @Query("DELETE FROM tracked_players")
    suspend fun clearAllPlayers()

    /* PvP Data */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReportContainer(pvPData: List<ReportContainer>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKDDataContainer(pvPData: List<ReportDataIn>)

    @Query("DELETE FROM report_data WHERE victimData LIKE '%' || :playerName || '%' OR killerData LIKE '%' || :playerName || '%'")
    suspend fun deletePlayerReportsData(playerName: String)

    @Query("DELETE FROM tracked_players_data WHERE victim LIKE '%' || :playerName || '%' OR attacker LIKE '%' || :playerName || '%'")
    suspend fun deletePlayerDataContainer(playerName: String)

    // Sample data to get default min/max fame range & reports count
    @Query("SELECT * FROM tracked_players_data")
    fun getPvPData(): Flow<List<ReportContainer>>

    @Query("DELETE FROM tracked_players_data")
    suspend fun clearAllReports()

    fun getPvPDataQuery(
        query: MutableList<String>,
        minFameRange: Long,
        maxFameRange: Long,
        reportType: ReportType,
        sortBy: SortBy,
        order: SortOrder
    ): Flow<List<ReportsDataOut>> {
        when (reportType) {
            ReportType.BOTH -> {
                return if (sortBy == SortBy.DATE && order == SortOrder.DESC) {
                    return getPvPDataBothByDateDesc(query, minFameRange, maxFameRange)
                } else if (sortBy == SortBy.DATE && order == SortOrder.ASC) {
                    getPvPDataBothByDateAsc(query, minFameRange, maxFameRange)
                } else if (sortBy == SortBy.FAME && order == SortOrder.DESC) {
                    getPvPDataBothByFameDesc(query, minFameRange, maxFameRange)
                } else {
                    getPvPDataBothByFameAsc(query, minFameRange, maxFameRange)
                }
            }
            ReportType.KILLS -> {
                return if (sortBy == SortBy.DATE && order == SortOrder.DESC) {
                    getPvPDataKillsByDateDesc(query, minFameRange, maxFameRange)
                } else if (sortBy == SortBy.DATE && order == SortOrder.ASC) {
                    getPvPDataKillsByDateAsc(query, minFameRange, maxFameRange)
                } else if (sortBy == SortBy.FAME && order == SortOrder.DESC) {
                    getPvPDataKillsByFameDesc(query, minFameRange, maxFameRange)
                } else {
                    getPvPDataKillsByFameAsc(query, minFameRange, maxFameRange)
                }
            }
            ReportType.DEATHS -> {
                return if (sortBy == SortBy.DATE && order == SortOrder.DESC) {
                    getPvPDataDeathsByDateDesc(query, minFameRange, maxFameRange)
                } else if (sortBy == SortBy.DATE && order == SortOrder.ASC) {
                    getPvPDataDeathsByDateAsc(query, minFameRange, maxFameRange)
                } else if (sortBy == SortBy.FAME && order == SortOrder.DESC) {
                    getPvPDataDeathsByFameDesc(query, minFameRange, maxFameRange)
                } else {
                    getPvPDataDeathsByFameAsc(query, minFameRange, maxFameRange)
                }
            }
        }
    }

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.attacker IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY eventId DESC"
    )
    fun getPvPDataKillsByDateDesc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.attacker IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY eventId ASC"
    )
    fun getPvPDataKillsByDateAsc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.attacker IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY fame DESC"
    )
    fun getPvPDataKillsByFameDesc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.attacker IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY fame ASC"
    )
    fun getPvPDataKillsByFameAsc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.victim IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY eventId DESC"
    )
    fun getPvPDataDeathsByDateDesc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.victim IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY eventId ASC"
    )
    fun getPvPDataDeathsByDateAsc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.victim IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY fame DESC"
    )
    fun getPvPDataDeathsByFameDesc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND tracked_players_data.victim IN (:query) AND fame BETWEEN :minFame AND :maxFame ORDER BY fame ASC"
    )
    fun getPvPDataDeathsByFameAsc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND (tracked_players_data.victim IN (:query) OR tracked_players_data.attacker IN (:query)) AND fame BETWEEN :minFame AND :maxFame ORDER BY eventId DESC"
    )
    fun getPvPDataBothByDateDesc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND (tracked_players_data.victim IN (:query) OR tracked_players_data.attacker IN (:query)) AND fame BETWEEN :minFame AND :maxFame ORDER BY eventId ASC"
    )
    fun getPvPDataBothByDateAsc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND (tracked_players_data.victim IN (:query) OR tracked_players_data.attacker IN (:query)) AND fame BETWEEN :minFame AND :maxFame ORDER BY fame DESC"
    )
    fun getPvPDataBothByFameDesc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>

    @Transaction
    @Query(
        "SELECT * FROM tracked_players_data, report_data WHERE tracked_players_data.eventId == report_data.eventId " +
                "AND (tracked_players_data.victim IN (:query) OR tracked_players_data.attacker IN (:query)) AND fame BETWEEN :minFame AND :maxFame ORDER BY fame ASC"
    )
    fun getPvPDataBothByFameAsc(query: MutableList<String>, minFame: Long, maxFame: Long): Flow<List<ReportsDataOut>>
}