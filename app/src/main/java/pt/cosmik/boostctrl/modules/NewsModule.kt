package pt.cosmik.boostctrl.modules

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pt.cosmik.boostctrl.ui.news.NewsViewModel
import pt.cosmik.boostctrl.ui.news.detail.NewsDetailViewModel

var newsModule = module {
    viewModel { NewsViewModel(get()) }
    viewModel { NewsDetailViewModel() }
}