package pt.cosmik.boostctrl.modules

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pt.cosmik.boostctrl.ui.teams.TeamsViewModel

var teamsModule = module {
    viewModel { TeamsViewModel(get()) }
}