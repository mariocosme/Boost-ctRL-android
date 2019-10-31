package pt.cosmik.boostctrl.ui.standings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.TournamentRanking
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.ui.common.views.RankingItemDescriptor
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class StandingsViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(StandingsFragmentViewState())
    val viewEffect = SingleLiveEvent<StandingsFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    fun processEvent(event: StandingsFragmentEvent) {
        when (event) {
            StandingsFragmentEvent.ViewCreated -> loadStandings()
            StandingsFragmentEvent.DidTriggerRefresh -> loadStandings()
            is StandingsFragmentEvent.DidSelectRankingDescriptor -> {
                /**
                 * TODO:
                 * show isLoading
                 * boostCtrlRepository.getTeamByName(event.descriptor.teamName) and then make view navigate to global TeamFragment with that arg
                 * hide isLoading
                 */
            }
        }
    }

    private fun loadStandings() {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(boostCtrlRepository.getRankings().subscribe({
            viewState.value = viewState.value?.copy(isLoading = false, rankingItems = it)
        }, {
            Crashlytics.logException(it)
            viewState.value = viewState.value?.copy(isLoading = false)
            viewEffect.value = StandingsFragmentViewEffect.ShowError("Something went wrong trying to obtain the rankings.")
        }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class StandingsFragmentViewState(
        val isLoading: Boolean = false,
        val rankingItems: List<TournamentRanking> = listOf()
    )

    sealed class StandingsFragmentViewEffect {
        data class ShowError(val message: String): StandingsFragmentViewEffect()
    }

    sealed class StandingsFragmentEvent {
        object ViewCreated: StandingsFragmentEvent()
        object DidTriggerRefresh: StandingsFragmentEvent()
        data class DidSelectRankingDescriptor(val descriptor: RankingItemDescriptor): StandingsFragmentEvent()
    }
}