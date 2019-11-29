package pt.cosmik.boostctrl.ui.competitions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.Competition
import pt.cosmik.boostctrl.models.CompetitionType
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class CompetitionsViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(CompetitionsFragmentViewState())
    val viewEffect = SingleLiveEvent<CompetitionsFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    fun processEvent(event: CompetitionsFragmentEvent) {
        when (event) {
            CompetitionsFragmentEvent.ViewCreated -> {
                viewState.value?.competitionItems?.let {
                    if (it.isEmpty()) {
                        loadCompetitions()
                    }
                    else {
                        viewState.value = viewState.value?.copy(competitionItems = it)
                    }
                }
            }
            CompetitionsFragmentEvent.DidTriggerRefresh -> loadCompetitions()
            is CompetitionsFragmentEvent.DidSelectCompetition -> {
                event.competition.competitionId?.let { competitionId ->
                    viewState.value = viewState.value?.copy(isLoading = true)
                    disposables.add(boostCtrlRepository.getCompetition(competitionId).subscribe ({
                        viewState.value = viewState.value?.copy(isLoading = false)
                        viewEffect.value = CompetitionsFragmentViewEffect.PresentCompetitionFragment(it)
                    }, {
                        Crashlytics.logException(it)
                        viewState.value = viewState.value?.copy(isLoading = false)
                        viewEffect.value = CompetitionsFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected competition.")
                    }))
                }
            }
        }
    }

    private fun loadCompetitions() {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(boostCtrlRepository.getAllCompetitions().subscribe ({
            viewState.value = viewState.value?.copy(
                isLoading = false,
                competitionItems = generateCompetitionDescriptorItems(it)
            )
        }, {
            Crashlytics.logException(it)
            viewState.value = viewState.value?.copy(isLoading = false)
            viewEffect.value = CompetitionsFragmentViewEffect.ShowError("Something went wrong trying to obtain the competitions.")
        }))
    }

    private fun generateCompetitionDescriptorItems(competitions: List<Competition>): List<CompetitionDescriptorItem> {
        val descriptors = mutableListOf(
            CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION_GROUP, "Premier"),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.SPACER),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION_GROUP, "Major"),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.SPACER),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION_GROUP, "Minor"),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.SPACER),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION_GROUP, "Other"),
            CompetitionDescriptorItem(CompetitionDescriptorItemType.SPACER)
        )

        var premiers = 0
        var majors = 0
        var minors = 0
        var others = 0
        competitions.forEach { competition ->
            competition.type?.let { type ->
                when (type) {
                    CompetitionType.PREMIER -> {
                        // Index will be always 1
                        descriptors.add(1, CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION, competition.nameAbbreviated, competition.id))
                        premiers++
                    }
                    CompetitionType.MAJOR -> {
                        val index = descriptors.indexOfFirst { it.competitionDescriptorType == CompetitionDescriptorItemType.COMPETITION_GROUP && it.competitionText == "Major" }
                        descriptors.add(index+1, CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION, competition.nameAbbreviated, competition.id))
                        majors++
                    }
                    CompetitionType.MINOR -> {
                        val index = descriptors.indexOfFirst { it.competitionDescriptorType == CompetitionDescriptorItemType.COMPETITION_GROUP && it.competitionText == "Minor" }
                        descriptors.add(index+1, CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION, competition.nameAbbreviated, competition.id))
                        minors++
                    }
                    CompetitionType.OTHER -> {
                        val index = descriptors.indexOfFirst { it.competitionDescriptorType == CompetitionDescriptorItemType.COMPETITION_GROUP && it.competitionText == "Other" }
                        descriptors.add(index+1, CompetitionDescriptorItem(CompetitionDescriptorItemType.COMPETITION, competition.nameAbbreviated, competition.id))
                        others++
                    }
                }
            }
        }

        if (premiers == 0) {
            descriptors.removeAt(0)
            descriptors.removeAt(0)
        }
        if (majors == 0) {
            val index = descriptors.indexOfFirst { it.competitionDescriptorType == CompetitionDescriptorItemType.COMPETITION_GROUP && it.competitionText == "Major" }
            descriptors.removeAt(index)
            descriptors.removeAt(index)
        }
        if (minors == 0) {
            val index = descriptors.indexOfFirst { it.competitionDescriptorType == CompetitionDescriptorItemType.COMPETITION_GROUP && it.competitionText == "Minor" }
            descriptors.removeAt(index)
            descriptors.removeAt(index)
        }
        if (others == 0) {
            descriptors.removeAt(descriptors.size-1)
            descriptors.removeAt(descriptors.size-1)
        }

        if (descriptors[descriptors.size-1].competitionDescriptorType == CompetitionDescriptorItemType.SPACER) {
            descriptors.removeAt(descriptors.size-1)
        }

        return descriptors
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class CompetitionsFragmentViewState(
        val isLoading: Boolean = false,
        val competitionItems: List<CompetitionDescriptorItem> = listOf()
    )

    sealed class CompetitionsFragmentViewEffect {
        data class ShowError(val message: String): CompetitionsFragmentViewEffect()
        data class PresentCompetitionFragment(val competition: Competition): CompetitionsFragmentViewEffect()
    }

    sealed class CompetitionsFragmentEvent {
        object ViewCreated: CompetitionsFragmentEvent()
        object DidTriggerRefresh: CompetitionsFragmentEvent()
        data class DidSelectCompetition(val competition: CompetitionDescriptorItem): CompetitionsFragmentEvent()
    }
}