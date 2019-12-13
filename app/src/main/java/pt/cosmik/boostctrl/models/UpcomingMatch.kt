package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class UpcomingMatch(
    @SerializedName("_id") val id: String,
    @SerializedName("lastRequested") val lastRequested: Date,
    @SerializedName("dateTime") val dateTime: Date,
    @SerializedName("tournamentName") val tournamentName: String?,
    @SerializedName("tournamentImage") val tournamentImage: String?,
    @SerializedName("homeTeam") val homeTeam: Team?,
    @SerializedName("awayTeam") val awayTeam: Team?,
    @SerializedName("versus") val versus: String?
): Serializable