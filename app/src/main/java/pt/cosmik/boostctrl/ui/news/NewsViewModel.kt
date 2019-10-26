package pt.cosmik.boostctrl.ui.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.repositories.OctaneggRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class NewsViewModel(val octaneggRepository: OctaneggRepository) : ViewModel() {

    val viewState = MutableLiveData(NewsFragmentViewState())
    val viewEffect = SingleLiveEvent<NewsFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    init {
        loadLatestNews()
    }

    fun processEvent(event: NewsFragmentEvent) {
        when (event) {
            NewsFragmentEvent.DidTriggerRefresh -> {
                loadLatestNews()
            }
        }
    }

    private fun loadLatestNews() {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(octaneggRepository.getLatestNews().subscribe({
            viewState.value = viewState.value?.copy(isLoading = false)
            // TODO: add items to the view
        }, {
            // TODO: analytics
            viewState.value = viewState.value?.copy(isLoading = false)
            viewEffect.value = NewsFragmentViewEffect.ShowError("Something went wrong trying to obtain the latest news.")
        }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class NewsFragmentViewState(
        val isLoading: Boolean = false
    )

    sealed class NewsFragmentViewEffect {
        data class ShowError(val message: String): NewsFragmentViewEffect()
    }

    sealed class NewsFragmentEvent {
        object DidTriggerRefresh: NewsFragmentEvent()
    }
}