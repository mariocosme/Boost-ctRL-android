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
    @SerializedName("discord") val discord: String?,
    @SerializedName("liquipediaUrl") val liquipediaUrl: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("manager") val manager: String?,
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: Region?,
    @SerializedName("roster") val roster: List<Person>?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("twitch") val twitch: String?,
    @SerializedName("twitter") val twitter: String?,
    @SerializedName("youtube") val youtube: String?
): Serializable