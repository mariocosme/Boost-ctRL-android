package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class NewsItem(
    @SerializedName("sourceId") val id: String,
    @SerializedName("_id") val _id: String,
    @SerializedName("date") val date: Date?,
    @SerializedName("author") val author: String = "Unknown",
    @SerializedName("twitter") val twitter: String?,
    @SerializedName("hyphenated") val hyphenated: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("article") val article: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("publish") val publish: Int?,
    @SerializedName("lastRequested") val lastRequested: Date?
): Serializable