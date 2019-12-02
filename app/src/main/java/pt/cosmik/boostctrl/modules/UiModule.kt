package pt.cosmik.boostctrl.modules

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pt.cosmik.boostctrl.ui.competitions.CompetitionsViewModel
import pt.cosmik.boostctrl.ui.competitions.detail.CompetitionViewModel
import pt.cosmik.boostctrl.ui.matches.MatchesViewModel
import pt.cosmik.boostctrl.ui.matches.detail.MatchViewModel
import pt.cosmik.boostctrl.ui.news.NewsViewModel
import pt.cosmik.boostctrl.ui.news.detail.NewsDetailViewModel
import pt.cosmik.boostctrl.ui.person.PersonViewModel
import pt.cosmik.boostctrl.ui.teams.TeamsViewModel
import pt.cosmik.boostctrl.ui.teams.detail.TeamViewModel

var uiModule = module {
    viewModel { TeamsViewModel(get()) }
    viewModel { TeamViewModel(get()) }

    viewModel { NewsViewModel(get()) }
    viewModel { NewsDetailViewModel(get()) }

    viewModel { CompetitionsViewModel(get()) }
    viewModel { CompetitionViewModel(get()) }

    viewModel { MatchesViewModel(get()) }
    viewModel { MatchViewModel(get()) }

    viewModel { PersonViewModel() }
}