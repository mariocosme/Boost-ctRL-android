package pt.cosmik.boostctrl.utils

class Constants {
    companion object K {
        const val LOG_TAG = "BOOST_CTRL"
        const val THROTTLE_SINGLE_CLICK_MILLISECONDS: Long = 500

        const val OCTANE_GG_API = "https://api.octane.gg/api"

        const val BOOST_CTRL_API = "https://boost-ctrl-be-staging.herokuapp.com" // TODO: add build flavor variables for staging/production
    }
}