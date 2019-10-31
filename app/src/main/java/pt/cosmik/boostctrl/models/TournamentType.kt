package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName

enum class TournamentType {
    @SerializedName("0") RLCS,
    @SerializedName("1") RLRS
}