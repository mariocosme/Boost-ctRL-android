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
            TeamsFragmentEvent.ViewCreated -> loadActiveTeams()
        }
    }

    private fun loadActiveTeams() {
        viewState.value = viewState.value?.copy(isLoading = false)

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
    }

    sealed class TeamsFragmentEvent {
        object ViewCreated: TeamsFragmentEvent()
    }

}