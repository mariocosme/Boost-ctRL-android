package pt.cosmik.boostctrl.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        private const val datePattern = "dd/MM/yyyy"

        fun getDateFormatter(): DateFormat {
            return SimpleDateFormat(datePattern, Locale.getDefault())
        }
    }
}