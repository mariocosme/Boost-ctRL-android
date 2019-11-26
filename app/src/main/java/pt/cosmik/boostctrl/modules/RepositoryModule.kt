package pt.cosmik.boostctrl.modules

import org.koin.dsl.module
import pt.cosmik.boostctrl.repositories.BoostCtrlRepoImpl
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository

val repositoryModule = module {

    single<BoostCtrlRepository> { BoostCtrlRepoImpl(get()) }

}