package pt.cosmik.boostctrl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.multidex.MultiDexApplication
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pt.cosmik.boostctrl.modules.repositoryModule
import pt.cosmik.boostctrl.modules.restModule
import pt.cosmik.boostctrl.modules.uiModule
import kotlin.math.roundToInt


class BoostCtrlApplication : MultiDexApplication() {

    companion object {
        val instance = BoostCtrlApplication()
    }

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initNotificationChannels()
        JodaTimeAndroid.init(this)
    }

    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.in_app_channel_name)
            val descriptionText = getString(R.string.in_app_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.in_app_channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@BoostCtrlApplication)
            modules(listOf(
                restModule,
                repositoryModule,
                uiModule
            ))
        }
    }

    fun dpToPx(dp: Int): Int {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(displayMetrics)
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}