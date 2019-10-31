package pt.cosmik.boostctrl.models

import com.google.gson.annotations.SerializedName
import java.util.*

class UpdatedTime(
    @SerializedName("_id") val id: String,
    @SerializedName("updateKind") val updateKind: UpdateTimeKind,
    @SerializedName("lastRun") val lastRun: Date
)

enum class UpdateTimeKind {
    @SerializedName("0") RANKINGS
}