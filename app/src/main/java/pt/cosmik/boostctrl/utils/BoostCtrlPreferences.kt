package pt.cosmik.boostctrl.utils

import android.content.Context
import android.content.SharedPreferences

class BoostCtrlPreferences {
    companion object {
        val instance = BoostCtrlPreferences()

        // Preferences keys
        const val BRACKETS_IN_DEVELOPMENT_WARNING = "BRACKETS_IN_DEVELOPMENT_WARNING"
    }

    private lateinit var prefs: SharedPreferences

    fun initWithContext(context: Context) {
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getPrefs(): SharedPreferences = prefs
}