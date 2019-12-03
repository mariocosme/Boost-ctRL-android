package pt.cosmik.boostctrl.ui.info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.BracketContainer
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class InfoViewModel(private val boostCtrlRepository: BoostCtrlRepository) : ViewModel() {

    val viewState = MutableLiveData(InfoFragmentViewState())
    val viewEffect = SingleLiveEvent<InfoFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    fun processEvent(event: InfoFragmentEvent) {
        when (event) {
            InfoFragmentEvent.ViewCreated -> {

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class InfoFragmentViewState(
        val isLoading: Boolean = false
    )

    sealed class InfoFragmentViewEffect {
        data class ShowError(val message: String): InfoFragmentViewEffect()
    }

    sealed class InfoFragmentEvent {
        object ViewCreated: InfoFragmentEvent()
    }
}