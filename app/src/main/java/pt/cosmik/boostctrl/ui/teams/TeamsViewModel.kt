package pt.cosmik.boostctrl.ui.teams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class TeamsViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(TeamsFragmentViewState())
    val viewEffect = SingleLiveEvent<TeamsFragmentViewEffect>()
    val disposables = CompositeDisposable()

    fun processEvent(event: TeamsFragmentEvent) {
        when (event) {
            TeamsFragmentEvent.ViewCreated -> {
                viewState.value?.teams?.let {
                    if (it.isEmpty()) {
                        loadActiveTeams()
                    }
                    else {
                        viewState.value = viewState.value?.copy(teams = it)
                    }
                }
            }
            is TeamsFragmentEvent.DidSelectTeam -> {
                viewState.value = viewState.value?.copy(isLoading = true)
                disposables.add(boostCtrlRepository.getTeam(event.team.name).subscribe ({
                    viewState.value = viewState.value?.copy(isLoading = false)
                    if (it == null) {
                        viewEffect.value = TeamsFragmentViewEffect.ShowError("Cannot obtain the selected team.")
                    }
                    else {
                        viewEffect.value = TeamsFragmentViewEffect.PresentTeamFragment(it)
                    }
                }, {
                    Crashlytics.logException(it)
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = TeamsFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected team.")
                }))
            }
            TeamsFragmentEvent.DidTriggerRefresh -> loadActiveTeams()
        }
    }

    private fun loadActiveTeams() {
        viewState.value = viewState.value?.copy(isLoading = true)

        disposables.add(boostCtrlRepository.getActiveTeams().subscribe ({
            viewState.value = viewState.value?.copy(isLoading = false, teams = it)
        }, {
            Crashlytics.logException(it)
            viewState.value = viewState.value?.copy(isLoading = false)
            viewEffect.value = TeamsFragmentViewEffect.ShowError("Something went wrong trying to obtain the list of active teams.")
        }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class TeamsFragmentViewState(
        val isLoading: Boolean = false,
        val teams: List<Team> = listOf()
    )

    sealed class TeamsFragmentViewEffect {
        data class ShowError(val error: String): TeamsFragmentViewEffect()
        data class PresentTeamFragment(val team: Team): TeamsFragmentViewEffect()
    }

    sealed class TeamsFragmentEvent {
        object ViewCreated: TeamsFragmentEvent()
        object DidTriggerRefresh: TeamsFragmentEvent()
        data class DidSelectTeam(val team: Team): TeamsFragmentEvent()
    }

}