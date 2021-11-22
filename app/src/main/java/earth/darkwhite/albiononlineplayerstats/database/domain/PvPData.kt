package earth.darkwhite.albiononlineplayerstats.database.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import earth.darkwhite.albiononlineplayerstats.database.domain.PvPData.KDData.Equipment.EquipmentData
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Entity(tableName = "tracked_players_data")
@Parcelize
data class PvPData(
    @PrimaryKey
    @Json(name = "EventId")
    val eventId: Long,
    val numberOfParticipants: Int,
    val groupMemberCount: Int,
    @Json(name = "TimeStamp")
    val time: String,
    @Json(name = "Version")
    val version: Int,
    @Json(name = "TotalVictimKillFame")
    val fame: Long,
    @Json(name = "Location")
    val location: String?,
    @Json(name = "GvGMatch")
    val gvgMatch: Boolean?,
    @Json(name = "KillArea")
    val killArea: String?,
    @Json(name = "Killer")
    val killerData: KDData?,
    @Json(name = "Victim")
    val victimData: KDData,
    @Json(name = "Participants")
    val participants: List<KDData>,
    var isTheKiller: Boolean = false
) : Parcelable {

    @JsonClass(generateAdapter = true)
    @Parcelize
    data class KDData(
        @Json(name = "AverageItemPower")
        val avgIP: Double,
        @Json(name = "Name")
        val name: String,
        @Json(name = "Id")
        val userId: String,
        @Json(name = "GuildName")
        val guildName: String?,
        @Json(name = "GuildId")
        val guildId: String?,
        @Json(name = "AllianceName")
        val allianceName: String?,
        @Json(name = "AllianceId")
        val allianceId: String?,
        @Json(name = "AllianceTag")
        val allianceTag: String?,
        @Json(name = "Avatar")
        val avatar: String?,
        @Json(name = "AvatarRing")
        val avatarRing: String?,
        @Json(name = "Equipment")
        val equipment: Equipment?,
        @Json(name = "SupportHealingDone")
        val healingDone: Float?,
        @Json(name = "DamageDone")
        val dmgDone: Float?,
        @Json(name = "Inventory")
        val loot: List<EquipmentData?>
    ) : Parcelable {
        @JsonClass(generateAdapter = true)
        @Parcelize
        data class Equipment(
            @Json(name = "MainHand")
            val mainHand: EquipmentData?,
            @Json(name = "OffHand")
            val offHand: EquipmentData?,
            @Json(name = "Head")
            val head: EquipmentData?,
            @Json(name = "Armor")
            val armor: EquipmentData?,
            @Json(name = "Shoes")
            val shoes: EquipmentData?,
            @Json(name = "Bag")
            val bag: EquipmentData?,
            @Json(name = "Cape")
            val cape: EquipmentData?,
            @Json(name = "Mount")
            val mount: EquipmentData?,
            @Json(name = "Potion")
            val potion: EquipmentData?,
            @Json(name = "Food")
            val food: EquipmentData?
        ) : Parcelable {
            @JsonClass(generateAdapter = true)
            @Parcelize
            data class EquipmentData(
                @Json(name = "Count")
                val count: Int,
                @Json(name = "Quality")
                val quality: Int,
                @Json(name = "Type")
                val type: String,
                val itemId: String = "$type.png?size=100&quality=$quality"
            ) : Parcelable
        }
    }
}




