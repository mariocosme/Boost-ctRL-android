package pt.cosmik.boostctrl.modules

import org.koin.dsl.module
import pt.cosmik.boostctrl.repositories.OctaneggRepoImpl
import pt.cosmik.boostctrl.repositories.OctaneggRepository

val repositoryModule = module {

    single<OctaneggRepository> { OctaneggRepoImpl(get()) }

}