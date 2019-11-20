package pt.cosmik.boostctrl

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pt.cosmik.boostctrl.modules.*

class BoostCtrlApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@BoostCtrlApplication)
            modules(listOf(
                restModule,
                repositoryModule,
                teamsModule,
                newsModule,
                standingsModule,
                matchesModule
            ))
        }
    }

}