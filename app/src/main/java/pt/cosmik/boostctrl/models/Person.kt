package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Person(
    @SerializedName("_id") val id: String,
    @SerializedName("images") val images: List<String>?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("countryIcon") val countryIcon: String?,
    @SerializedName("birthdate") val birthDate: Date?,
    @SerializedName("currentTeam") val currentTeam: String?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("role") val role: Any?, // Can be either an Int enum`RosterPlayerRole` or a String
    @SerializedName("twitter") val twitter: String?,
    @SerializedName("twitch") val twitch: String?,
    @SerializedName("youtube") val youtube: String?,
    @SerializedName("discord") val discord: String?,
    @SerializedName("steam") val steam: String?,
    @SerializedName("instagram") val instagram: String?,
    @SerializedName("liquipediaUrl") val liquipediaUrl: String?,
    @SerializedName("summary") val summary: String?
): Serializable