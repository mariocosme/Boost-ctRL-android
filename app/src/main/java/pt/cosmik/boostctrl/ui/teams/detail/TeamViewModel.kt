package pt.cosmik.boostctrl.ui.teams.detail

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.RosterTeamPlayer
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class TeamViewModel(private val boostCtrlRepository: BoostCtrlRepository): ViewModel() {

    val viewState = MutableLiveData(TeamFragmentViewState())
    val viewEffect = SingleLiveEvent<TeamFragmentViewEffect>()

    private val disposables = CompositeDisposable()
    private var team: Team? = null
    private var context: Context? = null

    fun processEvent(event: TeamFragmentEvent) {
        when (event) {
            is TeamFragmentEvent.ViewCreated -> {
                context = event.context

                if (event.team == null) {
                    viewEffect.value = TeamFragmentViewEffect.ShowError("Something went wrong trying to present the chosen team.")
                }
                else {
                    team = event.team
                    viewState.value = viewState.value?.copy(
                        barTitle = event.team.name,
                        teamImages = event.team.images,
                        teamDescription = event.team.summary,
                        teamGeneralDetailItems = generateTeamGeneralDetailItemDescriptors(event.team),
                        teamRosterPlayerItems = generateTeamRosterPlayerItemDescriptors(event.team)
                    )
                }
            }
            is TeamFragmentEvent.SelectedRosterItem -> {
                event.item.playerNickname?.let { player ->
                    viewState.value = viewState.value?.copy(isLoading = true)

                    disposables.add(boostCtrlRepository.getPerson(player).subscribe ({
                        viewState.value = viewState.value?.copy(isLoading = false)
                        it?.let { person -> viewEffect.value = TeamFragmentViewEffect.PresentPersonFragment(person) }
                    }, {
                        Crashlytics.logException(it)
                        viewState.value = viewState.value?.copy(isLoading = false)
                        viewEffect.value = TeamFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected subject.")
                    }))
                }
            }
        }
    }

    private fun generateTeamGeneralDetailItemDescriptors(team: Team): List<TeamGeneralDetailListItemDescriptor> {
        val items = mutableListOf<TeamGeneralDetailListItemDescriptor>()
        items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.name), team.name))
        // team.createdAt?.let { createdAt -> items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.created_at), createdAt)) }
        team.region?.let { region -> items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.region), region.toString(), team.regionIcon)) }
        team.location?.let { location -> items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.location), location, team.locationIcon)) }
        team.manager?.let { manager -> items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.manager), manager)) }
        team.totalEarnings?.let { totalEarnings -> items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.total_earnings), totalEarnings)) }
        team.liquipediaRating?.let { liquipediaRating -> items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.liquipedia_rating), liquipediaRating)) }
        team.sponsors?.let { sponsors -> if (sponsors.isNotEmpty()) items.add(TeamGeneralDetailListItemDescriptor(context?.getString(R.string.sponsors), sponsors.joinToString(", "))) }
        return items
    }

    @SuppressLint("DefaultLocale")
    private fun generateTeamRosterPlayerItemDescriptors(team: Team): List<TeamRosterPlayerListItemDescriptor> {
        val items = mutableListOf<TeamRosterPlayerListItemDescriptor>()
        team.roster?.let { roster ->
            roster.forEach { person ->
                var playerName = person.nickname
                (person.role as? String)?.toInt()?.let { enumVal ->
                    playerName += " (${(RosterTeamPlayer.values()[enumVal]).name.toLowerCase().capitalize()})"
                }
                items.add(TeamRosterPlayerListItemDescriptor(
                    person.countryIcon,
                    playerName,
                    person.nickname,
                    person.joinDate
                ))
            }
        }
        return items
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class TeamFragmentViewState(
        val isLoading: Boolean = false,
        val barTitle: String? = null,
        val teamGeneralDetailItems: List<TeamGeneralDetailListItemDescriptor>? = null,
        val teamRosterPlayerItems: List<TeamRosterPlayerListItemDescriptor>? = null,
        val teamImages: List<String>? = null,
        val teamDescription: String? = null
    )

    sealed class TeamFragmentViewEffect {
        data class ShowError(val error: String): TeamFragmentViewEffect()
        data class PresentPersonFragment(val person: Person): TeamFragmentViewEffect()
    }

    sealed class TeamFragmentEvent {
        data class ViewCreated(val team: Team?, val context: Context?): TeamFragmentEvent()
        data class SelectedRosterItem(val item: TeamRosterPlayerListItemDescriptor): TeamFragmentEvent()
    }
}