package pt.cosmik.boostctrl.ui.matches.detail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.ui.common.KeyValueListItemDescriptor
import pt.cosmik.boostctrl.utils.SingleLiveEvent
import java.text.DateFormat
import java.util.*

class MatchViewModel(private val boostCtrlRepository: BoostCtrlRepository): ViewModel() {

    val viewState = MutableLiveData(MatchFragmentViewState())
    val viewEffect = SingleLiveEvent<MatchFragmentViewEffect>()

    private val disposables = CompositeDisposable()
    private var match: UpcomingMatch? = null
    private var context: Context? = null

    fun processEvent(event: MatchFragmentEvent) {
        when (event) {
            is MatchFragmentEvent.ViewCreated -> {
                context = event.context
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
                        matchDetailsItems = generateMatchDetailItems(event.match),
                        teamRostersItems = generateMatchTeamRosterItems(event.match),
                        isLive = event.match.dateTime.before(Date())
                    )
                }
            }
            is MatchFragmentEvent.SelectedPlayer -> {
                viewState.value = viewState.value?.copy(isLoading = true)
                disposables.add(boostCtrlRepository.getPerson(event.playerNickname).subscribe ({
                    viewState.value = viewState.value?.copy(isLoading = false)
                    it?.let { viewEffect.value = MatchFragmentViewEffect.PresentPersonFragment(it) }
                }, {
                    Crashlytics.logException(it)
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = MatchFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected player.")
                }))
            }
            MatchFragmentEvent.DidTriggerRefresh -> reloadMatch()
        }
    }

    private fun reloadMatch() {
        match?.let { currentMatch ->
            viewState.value = viewState.value?.copy(isLoading = true)
            disposables.add(boostCtrlRepository.getUpcomingAndOngoingMatch(currentMatch.id).subscribe ({
                match = it
                viewState.value = viewState.value?.copy(
                    barTitle = "${it.homeTeam?.name} vs. ${it.awayTeam?.name}",
                    homeTeamName = it.homeTeam?.name,
                    homeTeamImage = it.homeTeam?.mainImage,
                    awayTeamImage = it.awayTeam?.mainImage,
                    awayTeamName = it.awayTeam?.name,
                    matchDetailsItems = generateMatchDetailItems(it),
                    teamRostersItems = generateMatchTeamRosterItems(it),
                    isLive = it.dateTime.before(Date())
                )
                viewState.value = viewState.value?.copy(isLoading = false)
            }, {
                Crashlytics.logException(it)
                viewState.value = viewState.value?.copy(isLoading = false)
                viewEffect.value = MatchFragmentViewEffect.ShowError("Something went wrong trying to refresh the current match.")
            }))
        }
    }

    private fun generateMatchDetailItems(match: UpcomingMatch): List<KeyValueListItemDescriptor> {
        val items = mutableListOf<KeyValueListItemDescriptor>()
        items.add(KeyValueListItemDescriptor(
            context?.getString(R.string.date_when),
            "${DateFormat.getDateInstance(DateFormat.SHORT).format(match.dateTime)} ${DateFormat.getTimeInstance(DateFormat.SHORT).format(match.dateTime)}")
        )
        items.add(KeyValueListItemDescriptor(context?.getString(R.string.event), match.tournamentName, match.tournamentImage))
        return items
    }

    private fun generateMatchTeamRosterItems(match: UpcomingMatch): List<MatchTeamRosterItemDescriptor> {
        val items = mutableListOf<MatchTeamRosterItemDescriptor>()

        match.homeTeam?.roster?.let { homeRoster ->
            match.awayTeam?.roster?.let { awayRoster ->
                for (i in 0..2) {
                    val row = MatchTeamRosterItemDescriptor()
                    if (homeRoster.size > i) {
                        row.homePlayerNickname = homeRoster[i].nickname
                        row.homePlayerFlag = homeRoster[i].countryIcon
                    }
                    if (awayRoster.size > i) {
                        row.awayPlayerNickname = awayRoster[i].nickname
                        row.awayPlayerFlag = awayRoster[i].countryIcon
                    }

                    if (row.homePlayerNickname != null || row.awayPlayerNickname != null) items.add(row)
                }
            }
        }

        return items
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
        val isLive: Boolean = false,
        val matchDetailsItems: List<KeyValueListItemDescriptor>? = null,
        val teamRostersItems: List<MatchTeamRosterItemDescriptor>? = null
    )

    sealed class MatchFragmentViewEffect {
        data class ShowError(val error: String): MatchFragmentViewEffect()
        data class PresentPersonFragment(val person: Person): MatchFragmentViewEffect()
    }

    sealed class MatchFragmentEvent {
        data class ViewCreated(val match: UpcomingMatch?, val context: Context?): MatchFragmentEvent()
        data class SelectedPlayer(val playerNickname: String): MatchFragmentEvent()
        object DidTriggerRefresh: MatchFragmentEvent()
    }
}