package pt.cosmik.boostctrl.utils

class Constants {
    companion object K {
        const val LOG_TAG = "BOOST_CTRL"
        const val THROTTLE_SINGLE_CLICK_MILLISECONDS: Long = 500
        const val APP_PREFERENCES = "BOOST_CTRL_SHARED_PREFERENCES"

        const val BOOST_CTRL_API = "https://boost-ctrl-be-staging.herokuapp.com"

        // FCM
        const val FCM_NEWS_ITEM = "newsItem"
    }
}