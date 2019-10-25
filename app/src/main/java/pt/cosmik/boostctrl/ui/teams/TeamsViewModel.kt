package pt.cosmik.boostctrl.ui.teams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class TeamsViewModel : ViewModel() {

    val viewState = MutableLiveData(TeamsFragmentViewState())
    val viewEffect = SingleLiveEvent<TeamsFragmentViewEffect>()

    init {
        viewState.value = viewState.value?.copy(isLoading = false)
    }

    fun processEvent(event: TeamsFragmentEvent) {
        when (event) {
            TeamsFragmentEvent.CreatedView -> {}
        }
    }

    data class TeamsFragmentViewState(
        val isLoading: Boolean = false
    )

    sealed class TeamsFragmentViewEffect {
        data class ShowError(val error: String): TeamsFragmentViewEffect()
    }

    sealed class TeamsFragmentEvent {
        object CreatedView: TeamsFragmentEvent()
    }
}