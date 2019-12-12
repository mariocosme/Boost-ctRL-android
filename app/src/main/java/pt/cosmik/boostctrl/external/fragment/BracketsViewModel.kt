package pt.cosmik.boostctrl.external.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.external.model.ColumnData
import pt.cosmik.boostctrl.external.model.CompetitorData
import pt.cosmik.boostctrl.external.model.MatchData
import pt.cosmik.boostctrl.models.BracketContainer
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent
import java.util.*

class BracketsViewModel(private val boostCtrlRepository: BoostCtrlRepository): ViewModel() {

    val viewState = MutableLiveData(BracketsFragmentViewState())
    val viewEffect = SingleLiveEvent<BracketsFragmentViewEffect>()

    private val disposables = CompositeDisposable()
    private var bracketContainers: List<BracketContainer>? = null
    private var selectedContainer: String? = null
    private var selectedSection: String? = null

    fun processEvent(event: BracketsFragmentEvent) {
        when (event) {
            is BracketsFragmentEvent.ViewCreated -> {
                event.competitionId?.let { competitionId ->
                    viewState.value = viewState.value?.copy(isLoading = true)
                    disposables.add(boostCtrlRepository.getCompetitionBrackets(competitionId).subscribe({
                        viewState.value = viewState.value?.copy(isLoading = false)
                        it?.let { bracketContainers ->
                            this.bracketContainers = bracketContainers

                            if (bracketContainers.isEmpty()) {
                                viewEffect.value = BracketsFragmentViewEffect.ShowNoBracketsAvailable
                            }
                            else {
                                // TODO: chose 1st
                                loadBrackets(null, null)
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
            BracketsFragmentEvent.SelectedChangeContainerButton -> {
                bracketContainers?.let { containers ->
                    viewEffect.value = BracketsFragmentViewEffect.ShowDialogContainerPicker(containers.map { container -> container.title })
                }
            }
            BracketsFragmentEvent.SelectedChangeSectionButton -> {
                selectedContainer?.let { selectedContainer ->
                    bracketContainers?.let { containers ->
                        containers.firstOrNull { container -> container.title == selectedContainer }?.let { bracketContainer ->
                            bracketContainer.sections?.let { sections ->
                                viewEffect.value = BracketsFragmentViewEffect.ShowDialogSectionPicker(sections.map { section -> section.title })
                            }
                        }
                    }
                }
            }
            is BracketsFragmentEvent.SelectedContainer -> {
                selectedContainer = event.container
                selectedSection = null
            }
            is BracketsFragmentEvent.SelectedSection -> {
                selectedSection = event.section
                loadBrackets(selectedContainer, selectedSection)
            }
        }
    }

    private fun loadBrackets(selectedContainer: String?, selectedSection: String?) {
        val chosenSection = bracketContainers?.firstOrNull { bracketContainer -> bracketContainer.title == selectedContainer }?.sections?.firstOrNull { section -> section.title == selectedSection }
        chosenSection?.let { section ->
            val columns = ArrayList<ColumnData>()
            section.phases?.forEach { bracketPhase ->
                val matchData = ArrayList<MatchData>()
                bracketPhase.brackets?.forEach { bracket ->
                    val homeTeam = CompetitorData(bracket.homeTeam, bracket.homeTeamScore, bracket.homeTeamIcon)
                    val awayTeam = CompetitorData(bracket.awayTeam, bracket.awayTeamScore, bracket.awayTeamIcon)
                    val match = MatchData(homeTeam, awayTeam)
                    matchData.add(match)
                }

                columns.add(ColumnData(matchData))
            }

            viewEffect.value = BracketsFragmentViewEffect.ShowBrackets(columns)
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
        data class ShowDialogContainerPicker(val containers: List<String?>): BracketsFragmentViewEffect()
        data class ShowDialogSectionPicker(val sections: List<String?>): BracketsFragmentViewEffect()
    }

    sealed class BracketsFragmentEvent {
        data class ViewCreated(var competitionId: String?): BracketsFragmentEvent()
        object SelectedChangeContainerButton: BracketsFragmentEvent()
        data class SelectedContainer(val container: String?): BracketsFragmentEvent()
        object SelectedChangeSectionButton: BracketsFragmentEvent()
        data class SelectedSection(val section: String?): BracketsFragmentEvent()
    }
}