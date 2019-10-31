package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName

enum class Region {
    @SerializedName("0") EUROPE,
    @SerializedName("1") NORTH_AMERICA,
    @SerializedName("2") OCEANIA,
    @SerializedName("3") SOUTH_AMERICA;

    fun getRegionAbbreviation(): String {
        return when (this) {
            EUROPE -> "EU"
            NORTH_AMERICA -> "NA"
            OCEANIA -> "OCE"
            SOUTH_AMERICA -> "SAM"
        }
    }
}