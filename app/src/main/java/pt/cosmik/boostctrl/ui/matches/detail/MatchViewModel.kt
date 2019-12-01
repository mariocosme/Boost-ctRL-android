package pt.cosmik.boostctrl.ui.matches.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.utils.SingleLiveEvent
import java.text.DateFormat

class MatchViewModel: ViewModel() {

    val viewState = MutableLiveData(MatchFragmentViewState())
    val viewEffect = SingleLiveEvent<MatchFragmentViewEffect>()

    private val disposables = CompositeDisposable()
    private var match: UpcomingMatch? = null

    fun processEvent(event: MatchFragmentEvent) {
        when (event) {
            is MatchFragmentEvent.ViewCreated -> {
                if (event.match == null) {
                    viewEffect.value = MatchFragmentViewEffect.ShowError("Something went wrong trying to present the chosen match.")
                }
                else {
                    match = event.match
                    viewState.value = viewState.value?.copy(
                        barTitle = "${event.match.homeTeam?.name} vs. ${event.match.awayTeam?.name}",
                        homeTeamName = event.match.homeTeam?.name,
                        homeTeamImage = event.match.homeTeam?.mainImage,
                        awayTeamImage = event.match.awayTeam?.mainImage,
                        awayTeamName = event.match.awayTeam?.name,
                        tournamentName = event.match.tournamentName,
                        tournamentImage = event.match.tournamentImage,
                        matchDate = "${DateFormat.getDateInstance(DateFormat.SHORT).format(event.match.dateTime)} ${DateFormat.getTimeInstance(DateFormat.SHORT).format(event.match.dateTime)}"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class MatchFragmentViewState(
        val isLoading: Boolean = false,
        val barTitle: String? = null,
        val homeTeamName: String? = null,
        val awayTeamName: String? = null,
        val homeTeamImage: String? = null,
        val awayTeamImage: String? = null,
        val tournamentName: String? = null,
        val tournamentImage: String? = null,
        val matchDate: String? = null
    )

    sealed class MatchFragmentViewEffect {
        data class ShowError(val error: String): MatchFragmentViewEffect()
    }

    sealed class MatchFragmentEvent {
        data class ViewCreated(val match: UpcomingMatch?): MatchFragmentEvent()
    }
}