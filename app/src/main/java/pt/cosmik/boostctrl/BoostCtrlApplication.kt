package pt.cosmik.boostctrl

import androidx.multidex.MultiDexApplication
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pt.cosmik.boostctrl.modules.repositoryModule
import pt.cosmik.boostctrl.modules.restModule
import pt.cosmik.boostctrl.modules.uiModule

class BoostCtrlApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        JodaTimeAndroid.init(this)
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

}