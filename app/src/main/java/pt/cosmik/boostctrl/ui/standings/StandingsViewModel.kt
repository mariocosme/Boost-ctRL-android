package pt.cosmik.boostctrl.ui.standings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.zipWith
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.TournamentRanking
import pt.cosmik.boostctrl.models.UpdateTimeKind
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.ui.common.views.RankingItemDescriptor
import pt.cosmik.boostctrl.utils.DateUtils
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class StandingsViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(StandingsFragmentViewState())
    val viewEffect = SingleLiveEvent<StandingsFragmentViewEffect>()
    private val disposables = CompositeDisposable()
    private var context: Context? = null

    fun processEvent(event: StandingsFragmentEvent) {
        when (event) {
            is StandingsFragmentEvent.ViewCreated -> {
                context = event.context
                loadStandings()
            }
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

        disposables.add(boostCtrlRepository.getRankings().zipWith(boostCtrlRepository.getUpdatedTime(UpdateTimeKind.RANKINGS)).subscribe ({
            viewState.value = viewState.value?.copy(
                isLoading = false,
                rankingItems = it.first,
                lastUpdatedAt = context?.getString(R.string.last_updated_at, DateUtils.getDateFormatter(DateUtils.patternWithHourMinuteSeconds).format(it.second.lastRun))
            )
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
        val rankingItems: List<TournamentRanking> = listOf(),
        val lastUpdatedAt: String? = null
    )

    sealed class StandingsFragmentViewEffect {
        data class ShowError(val message: String): StandingsFragmentViewEffect()
    }

    sealed class StandingsFragmentEvent {
        data class ViewCreated(val context: Context?): StandingsFragmentEvent()
        object DidTriggerRefresh: StandingsFragmentEvent()
        data class DidSelectRankingDescriptor(val descriptor: RankingItemDescriptor): StandingsFragmentEvent()
    }
}