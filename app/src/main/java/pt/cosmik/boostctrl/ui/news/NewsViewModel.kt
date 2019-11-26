package pt.cosmik.boostctrl.ui.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class NewsViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(NewsFragmentViewState())
    val viewEffect = SingleLiveEvent<NewsFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    private var canLoadMore = true
    private var currentLoadedPage: Int? = null

    fun processEvent(event: NewsFragmentEvent) {
        when (event) {
            NewsFragmentEvent.DidTriggerRefresh -> loadLatestNews(0)
            NewsFragmentEvent.ViewCreated -> {
                viewState.value?.newsItems?.let {
                    if (it.isEmpty()) {
                        loadLatestNews(0)
                    }
                    else {
                        viewState.value = viewState.value?.copy(newsItems = it)
                    }
                }
            }
            NewsFragmentEvent.LoadMoreNewsItems -> {
                if (canLoadMore) {
                    loadLatestNews(currentLoadedPage!!+1, loadMore = true)
                }
            }
        }
    }

    private fun loadLatestNews(page: Int, loadMore: Boolean = false) {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(boostCtrlRepository.getNews(page).subscribe({
            currentLoadedPage = page
            canLoadMore = it.isNotEmpty() && it.size >= 10

            if (loadMore) {
                val allItems = viewState.value?.newsItems?.toMutableList()
                allItems?.addAll(it)
                allItems?.let {
                    viewState.value = viewState.value?.copy(isLoading = false, newsItems = allItems)
                }
            }
            else {
                viewState.value = viewState.value?.copy(isLoading = false, newsItems = it)
            }
        }, {
            Crashlytics.logException(it)
            viewState.value = viewState.value?.copy(isLoading = false)
            viewEffect.value = NewsFragmentViewEffect.ShowError("Something went wrong trying to obtain the latest news.")
        }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class NewsFragmentViewState(
        val isLoading: Boolean = false,
        val newsItems: List<NewsItem> = listOf()
    )

    sealed class NewsFragmentViewEffect {
        data class ShowError(val message: String): NewsFragmentViewEffect()
    }

    sealed class NewsFragmentEvent {
        object DidTriggerRefresh: NewsFragmentEvent()
        object ViewCreated: NewsFragmentEvent()
        object LoadMoreNewsItems: NewsFragmentEvent()
    }
}