package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TournamentRanking(
    @SerializedName("_id") val id: String,
    @SerializedName("region") val region: Region,
    @SerializedName("regionName") val regionName: String?,
    @SerializedName("tournament") val tournamentType: TournamentType,
    @SerializedName("tournamentName") val tournamentName: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("organizer") val organizer: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("rankings") val rankings: Ranking?
): Serializable

data class Ranking(
    @SerializedName("teams") val teams: List<String>?,
    @SerializedName("winLoss") val winLoss: List<String>?,
    @SerializedName("gamesWinLoss") val gamesWinLoss: List<String>?,
    @SerializedName("gamesDifference") val gamesDifference: List<String>?,
    @SerializedName("teamImages") val teamImages: List<String>?,
    @SerializedName("colors") val colors: List<String>?
): Serializable