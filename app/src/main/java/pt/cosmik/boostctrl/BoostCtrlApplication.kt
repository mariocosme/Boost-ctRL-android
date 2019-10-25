package pt.cosmik.boostctrl

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pt.cosmik.boostctrl.modules.teamsModule

class BoostCtrlApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@BoostCtrlApplication)
            modules(listOf(
                teamsModule
            ))
        }
    }

}