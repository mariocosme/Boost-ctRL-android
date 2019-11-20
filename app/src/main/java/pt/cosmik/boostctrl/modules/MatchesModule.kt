package pt.cosmik.boostctrl.modules

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pt.cosmik.boostctrl.ui.matches.MatchesViewModel

var matchesModule = module {
    viewModel { MatchesViewModel(get()) }
}