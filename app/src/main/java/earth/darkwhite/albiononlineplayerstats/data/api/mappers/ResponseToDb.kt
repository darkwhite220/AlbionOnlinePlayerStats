package earth.darkwhite.albiononlineplayerstats.data.api.mappers

import earth.darkwhite.albiononlineplayerstats.data.api.model.KDData
import earth.darkwhite.albiononlineplayerstats.data.api.model.ReportsResponse
import earth.darkwhite.albiononlineplayerstats.database.model.ReportContainer


/**
 * Convert server response to ReportContainer for Room DB entity
 * PvpData to ReportContainer
 */
fun ReportsResponse.toReportContainer(attacker: String? = "", victim: String): ReportContainer {
    return ReportContainer(
        eventId,
        attacker,
        victim,
        numberOfParticipants,
        groupMemberCount,
        time,
        version,
        fame,
        location,
        gvgMatch,
        killArea,
        isTheKiller
    )
}

/**
 * Convert server response to KDData for Room DB entity
 * ReportContainer to PvpData
 */
fun KDData.toReportsDataIn(): KDData {
    return KDData(
        avgIP,
        name,
        userId,
        guildName,
        guildId,
        allianceName,
        allianceId,
        allianceTag,
        avatar,
        avatarRing,
        equipment,
        healingDone,
        dmgDone,
        loot
    )
}
