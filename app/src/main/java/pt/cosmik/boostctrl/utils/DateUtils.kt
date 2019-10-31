package pt.cosmik.boostctrl.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        const val patternCommon = "dd/MM/yyyy"
        const val patternWithHourMinuteSeconds = "dd/MM/yyyy HH:mm:ss"

        fun getDateFormatter(pattern: String): DateFormat {
            return SimpleDateFormat(pattern, Locale.getDefault())
        }
    }
}