package pt.cosmik.boostctrl.utils

import okhttp3.Interceptor
import okhttp3.Response
import pt.cosmik.boostctrl.BuildConfig

class BoostCtrlInterceptor: Interceptor {

    private companion object {
        const val USER_AGENT = "User-Agent"
        const val USER_AGENT_VALUE = "Boost ctRL Android client"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val appVersion = "(${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE})"
        return chain.proceed(chain.request().newBuilder().addHeader(USER_AGENT, "$USER_AGENT_VALUE $appVersion").build())
    }

}