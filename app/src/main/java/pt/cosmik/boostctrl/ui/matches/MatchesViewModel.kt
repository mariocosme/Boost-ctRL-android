package pt.cosmik.boostctrl.ui.matches

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class MatchesViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(MatchesFragmentViewState())
    val viewEffect = SingleLiveEvent<MatchesFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    fun processEvent(event: MatchesFragmentEvent) {
        when (event) {
            MatchesFragmentEvent.DidTriggerRefresh -> loadUpcomingAndOngoingMatches()
            MatchesFragmentEvent.ViewCreated -> {
                viewState.value?.matches?.let {
                    if (it.isEmpty()) {
                        loadUpcomingAndOngoingMatches()
                    }
                    else {
                        viewState.value = viewState.value?.copy(matches = it)
                    }
                }
            }
            is MatchesFragmentEvent.DidSelectMatch -> {
                viewState.value = viewState.value?.copy(isLoading = true)

                disposables.add(boostCtrlRepository.getUpcomingAndOngoingMatch(event.match.id).subscribe ({
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = MatchesFragmentViewEffect.PresentUpcomingMatchFragment(it)
                }, {
                    Crashlytics.logException(it)
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = MatchesFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected match.")
                }))
            }
        }
    }

    private fun loadUpcomingAndOngoingMatches() {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(boostCtrlRepository.getUpcomingAndOngoingMatches().subscribe ({
            viewState.value = viewState.value?.copy(matches = it, isLoading = false)
        }, {
            Crashlytics.logException(it)
            viewState.value = viewState.value?.copy(isLoading = false)
            viewEffect.value = MatchesFragmentViewEffect.ShowError("Something went wrong trying to obtain the ongoing and upcoming matches.")
        }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class MatchesFragmentViewState(
        val isLoading: Boolean = false,
        var matches: List<UpcomingMatch> = listOf()
    )

    sealed class MatchesFragmentViewEffect {
        data class ShowError(val message: String): MatchesFragmentViewEffect()
        data class PresentUpcomingMatchFragment(val match: UpcomingMatch): MatchesFragmentViewEffect()
    }

    sealed class MatchesFragmentEvent {
        object ViewCreated: MatchesFragmentEvent()
        object DidTriggerRefresh: MatchesFragmentEvent()
        data class DidSelectMatch(val match: UpcomingMatch): MatchesFragmentEvent()
    }

}