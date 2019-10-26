package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class NewsDataContainer(
    @SerializedName("data") val data: List<NewsItem>
)

data class NewsItem(
    @SerializedName("id") val id: Int,
    @SerializedName("Date") val date: Date?,
    @SerializedName("Author") val author: String = "Unknown",
    @SerializedName("Twitter") val twitter: String?,
    @SerializedName("hyphenated") val hyphenated: String?,
    @SerializedName("Title") val title: String?,
    @SerializedName("Description") val description: String?,
    @SerializedName("Image") val image: String?,
    @SerializedName("Article") val firstArticle: String?,
    @SerializedName("Article2") val secondArticle: String?,
    @SerializedName("Publish") val publish: Int?
): Serializable