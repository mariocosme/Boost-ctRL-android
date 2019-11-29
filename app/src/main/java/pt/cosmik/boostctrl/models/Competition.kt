package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Competition(
    @SerializedName("_id") val id: String,
    @SerializedName("lastRequested") val lastRequested: Date,
    @SerializedName("type") val type: CompetitionType?,
    @SerializedName("name") val name: String?,
    @SerializedName("nameAbbreviated") val nameAbbreviated: String?,
    @SerializedName("liquipediaPage") val liquipediaPage: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("series") val series: String?,
    @SerializedName("seriesIcon") val seriesIcon: String?,
    @SerializedName("organizer") val organizer: String?,
    @SerializedName("sponsors") val sponsors: List<String>?,
    @SerializedName("mode") val mode: String?,
    @SerializedName("competitionType") val competitionType: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("locationIcon") val locationIcon: String?,
    @SerializedName("venue") val venue: String?,
    @SerializedName("prizePool") val prizePool: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("numberOfTeams") val numberOfTeams: String?,
    @SerializedName("brackets") val brackets: List<BracketContainer>?,
    @SerializedName("groupStandings") val groupStandings: GroupStanding?
): Serializable

enum class CompetitionType {
    @SerializedName("0") PREMIER,
    @SerializedName("1") MAJOR,
    @SerializedName("2") MINOR,
    @SerializedName("3") OTHER;
}

data class GroupStanding(
    @SerializedName("title") val title: String?,
    @SerializedName("teams") val teams: List<String>?,
    @SerializedName("winLoss") val winLoss: List<String>?,
    @SerializedName("gamesWinLoss") val gamesWinLoss: List<String>?,
    @SerializedName("gamesDifference") val gamesDifference: List<String>?,
    @SerializedName("teamImages") val teamImages: List<String>?,
    @SerializedName("colors") val colors: List<String>?
): Serializable

data class BracketContainer(
    @SerializedName("title") val title: String?,
    @SerializedName("sections") val sections: List<BracketSection>?
): Serializable

data class BracketSection(
    @SerializedName("title") val title: String?,
    @SerializedName("phases") val phases: List<BracketPhase>?
): Serializable

data class BracketPhase(
    @SerializedName("title") val title: String?,
    @SerializedName("brackets") val brackets: List<Bracket>?
): Serializable

data class Bracket(
    @SerializedName("homeTeam") val homeTeam: String?,
    @SerializedName("homeTeamScore") val homeTeamScore: String?,
    @SerializedName("homeTeamIcon") val homeTeamIcon: String?,
    @SerializedName("awayTeam") val awayTeam: String?,
    @SerializedName("awayTeamScore") val awayTeamScore: String?,
    @SerializedName("awayTeamIcon") val awayTeamIcon: String?
): Serializable