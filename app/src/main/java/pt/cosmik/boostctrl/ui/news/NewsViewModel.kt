package pt.cosmik.boostctrl.ui.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.repositories.OctaneggRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class NewsViewModel(private val octaneggRepository: OctaneggRepository) : ViewModel() {

    val viewState = MutableLiveData(NewsFragmentViewState())
    val viewEffect = SingleLiveEvent<NewsFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    fun processEvent(event: NewsFragmentEvent) {
        when (event) {
            NewsFragmentEvent.DidTriggerRefresh -> loadLatestNews()
            NewsFragmentEvent.ViewCreated -> {
                viewState.value?.newsItems?.let {
                    if (it.isEmpty()) {
                        loadLatestNews()
                    }
                    else {
                        viewState.value = viewState.value?.copy(newsItems = it)
                    }
                }
            }
        }
    }

    private fun loadLatestNews() {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(octaneggRepository.getLatestNews().subscribe({
            viewState.value = viewState.value?.copy(isLoading = false, newsItems = it)
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
    }
}