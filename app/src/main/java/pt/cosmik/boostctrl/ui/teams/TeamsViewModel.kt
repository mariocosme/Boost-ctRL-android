package pt.cosmik.boostctrl.ui.teams

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.repositories.OctaneggRepository
import pt.cosmik.boostctrl.utils.Constants
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class TeamsViewModel(octaneggRepository: OctaneggRepository) : ViewModel() {

    val viewState = MutableLiveData(TeamsFragmentViewState())
    val viewEffect = SingleLiveEvent<TeamsFragmentViewEffect>()
    val disposables = CompositeDisposable()

    init {
        viewState.value = viewState.value?.copy(isLoading = false)

        disposables.add(octaneggRepository.getLatestNews().subscribe ({
//            Log.d(Constants.LOG_TAG, "News items: $it")
        }, {
            Log.e(Constants.LOG_TAG, "Error: $it")
        }))
    }

    fun processEvent(event: TeamsFragmentEvent) {
        when (event) {
            TeamsFragmentEvent.CreatedView -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
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