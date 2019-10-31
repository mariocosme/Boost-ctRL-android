package pt.cosmik.boostctrl.modules

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pt.cosmik.boostctrl.ui.standings.StandingsViewModel

var standingsModule = module {
    viewModel { StandingsViewModel(get()) }
}