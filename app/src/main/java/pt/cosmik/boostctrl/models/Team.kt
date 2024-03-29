package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Team(
    @SerializedName("_id") val id: String,
    @SerializedName("lastRequested") val lastRequested: Date,
    @SerializedName("images") val images: List<String>?,
    @SerializedName("sponsors") val sponsors: List<String>?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("facebook") val facebook: String?,
    @SerializedName("discord") val discord: String?,
    @SerializedName("snapchat") val snapchat: String?,
    @SerializedName("steam") val steam: String?,
    @SerializedName("liquipediaUrl") val liquipediaUrl: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("locationIcon") val locationIcon: String?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("manager") val manager: String?,
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: Region?,
    @SerializedName("regionIcon") val regionIcon: String?,
    @SerializedName("roster") val roster: List<Person>?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("twitch") val twitch: String?,
    @SerializedName("twitter") val twitter: String?,
    @SerializedName("youtube") val youtube: String?,
    @SerializedName("totalEarnings") val totalEarnings: String?,
    @SerializedName("liquipediaRating") val liquipediaRating: String?
): Serializable

enum class RosterTeamPlayer {
    @SerializedName("0") COACH,
    @SerializedName("1") PLAYER,
    @SerializedName("2") SUBSTITUTE,
    @SerializedName("3") CAPTAIN;
}