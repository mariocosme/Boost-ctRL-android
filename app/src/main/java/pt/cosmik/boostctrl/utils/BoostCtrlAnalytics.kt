package pt.cosmik.boostctrl.utils

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import pt.cosmik.boostctrl.BuildConfig
import pt.cosmik.boostctrl.MainActivity

class BoostCtrlAnalytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var activity: Activity

    companion object {
        val instance = BoostCtrlAnalytics()
    }

    fun initWith(firebaseAnalytics: FirebaseAnalytics, activity: MainActivity) {
        this.firebaseAnalytics = firebaseAnalytics
        this.activity = activity
    }

    fun trackScreen(screenName: String) {
        if (BuildConfig.DEBUG) return

        firebaseAnalytics.setCurrentScreen(activity, screenName, null)
    }

    fun logEvent(eventId: String, eventName: String, eventType: String) {
        if (BuildConfig.DEBUG) return

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

}