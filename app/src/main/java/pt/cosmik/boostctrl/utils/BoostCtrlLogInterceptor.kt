package pt.cosmik.boostctrl.utils

import okhttp3.logging.HttpLoggingInterceptor
import pt.cosmik.boostctrl.BuildConfig

class BoostCtrlLogInterceptor {
    companion object {
        fun getLoggerInterceptor(): HttpLoggingInterceptor {
            val loggerInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggerInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
            return loggerInterceptor
        }
    }
}