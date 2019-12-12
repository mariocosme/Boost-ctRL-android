package pt.cosmik.boostctrl.external.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.external.model.ColumnData
import pt.cosmik.boostctrl.external.model.CompetitorData
import pt.cosmik.boostctrl.external.model.MatchData
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent
import java.util.*

class BracketsViewModel(private val boostCtrlRepository: BoostCtrlRepository): ViewModel() {

    val viewState = MutableLiveData(BracketsFragmentViewState())
    val viewEffect = SingleLiveEvent<BracketsFragmentViewEffect>()

    private val disposables = CompositeDisposable()

    fun processEvent(event: BracketsFragmentEvent) {
        when (event) {
            is BracketsFragmentEvent.ViewCreated -> {
                event.competitionId?.let { competitionId ->
                    viewState.value = viewState.value?.copy(isLoading = true)
                    disposables.add(boostCtrlRepository.getCompetitionBrackets(competitionId).subscribe({
                        viewState.value = viewState.value?.copy(isLoading = false)
                        it?.let { bracketContainers ->
                            if (bracketContainers.isEmpty()) {
                                viewEffect.value = BracketsFragmentViewEffect.ShowNoBracketsAvailable
                            }
                            else {
                                val columns = ArrayList<ColumnData>()

//                        TODO: implementation
//                        it?.let { bracketContainers ->
//                            val bracketContainer = bracketContainers[2]
//                            val bracketSection = bracketContainer.sections!![0]
//                            bracketSection.phases?.forEach { bracketPhase ->
//                                val matchDatas = ArrayList<MatchData>()
//                                bracketPhase.brackets?.forEach { bracket ->
//                                    val homeTeam = CompetitorData(bracket.homeTeam, bracket.homeTeamScore)
//                                    val awayTeam = CompetitorData(bracket.awayTeam, bracket.awayTeamScore)
//                                    val match = MatchData(homeTeam, awayTeam)
//                                    matchDatas.add(match)
//                                }
//
//                                columns.add(ColumnData(matchDatas))
//                            }
//                        }

                                viewEffect.value = BracketsFragmentViewEffect.ShowBrackets(columns)
                            }
                        }
                    }, {
                        Crashlytics.logException(it)
                        viewState.value = viewState.value?.copy(isLoading = false)
                        viewEffect.value =
                            BracketsFragmentViewEffect.ShowError("Something went wrong trying to obtain the brackets.")
                    }))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class BracketsFragmentViewState(
        val isLoading: Boolean = false
    )

    sealed class BracketsFragmentViewEffect {
        data class ShowError(val error: String): BracketsFragmentViewEffect()
        data class ShowBrackets(val brackets: ArrayList<ColumnData>): BracketsFragmentViewEffect()
        object ShowNoBracketsAvailable: BracketsFragmentViewEffect()
    }

    sealed class BracketsFragmentEvent {
        data class ViewCreated(var competitionId: String?): BracketsFragmentEvent()
    }
}