package earth.darkwhite.albiononlineplayerstats.database.mappers

import androidx.room.Embedded
import androidx.room.Relation
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.database.model.ReportContainer
import earth.darkwhite.albiononlineplayerstats.database.model.ReportDataIn

/**
 * DB KDData query result wrapper
 */
data class ReportsDataOut(
    @Embedded val reportContainer: ReportContainer,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "eventId"
    )
    val reportDataIn: ReportDataIn
)

/**
 * Unwrap ReportsData form Room DB to ReportContainer
 */
fun ReportsDataOut.toReportsResponse(): ReportsResponse {
    this.apply {
        return ReportsResponse(
            reportContainer.eventId,
            reportContainer.attacker,
            reportContainer.victim,
            reportContainer.numberOfParticipants,
            reportContainer.groupMemberCount,
            reportContainer.time,
            reportContainer.version,
            reportContainer.fame,
            reportContainer.location,
            reportContainer.gvgMatch,
            reportContainer.killArea,
            reportDataIn.killerData,
            reportDataIn.victimData,
            reportContainer.isTheKiller
        )
    }
}