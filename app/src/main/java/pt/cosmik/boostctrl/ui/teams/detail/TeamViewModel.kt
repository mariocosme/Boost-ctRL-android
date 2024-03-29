package pt.cosmik.boostctrl.ui.teams.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.RosterTeamPlayer
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.ui.common.KeyValueListItemDescriptor
import pt.cosmik.boostctrl.ui.common.SocialType
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
                        teamRosterPlayerItems = generateTeamRosterPlayerItemDescriptors(event.team),
                        teamSocialItems = generateTeamSocialItems(event.team)
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
            is TeamFragmentEvent.SelectedTeamSocial -> {
                val intent = when (event.socialType) {
                    SocialType.DISCORD -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.discord))
                    SocialType.INSTAGRAM -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.instagram))
                    SocialType.TWITTER -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.twitter))
                    SocialType.TWITCH -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.twitch))
                    SocialType.FACEBOOK -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.facebook))
                    SocialType.YOUTUBE -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.youtube))
                    SocialType.STEAM -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.steam))
                    SocialType.SNAPCHAT -> Intent(Intent.ACTION_VIEW, Uri.parse(team?.snapchat))
                }
                viewEffect.value = TeamFragmentViewEffect.OpenActivity(intent)
            }
        }
    }

    private fun generateTeamGeneralDetailItemDescriptors(team: Team): List<KeyValueListItemDescriptor> {
        val items = mutableListOf<KeyValueListItemDescriptor>()
        items.add(KeyValueListItemDescriptor(context?.getString(R.string.name), team.name))
        team.region?.let { region -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.region), region.toString(), team.regionIcon)) }
        team.location?.let { location -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.location), location, team.locationIcon)) }
        team.manager?.let { manager -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.manager), manager)) }
        team.totalEarnings?.let { totalEarnings -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.total_earnings), totalEarnings)) }
        team.liquipediaRating?.let { liquipediaRating -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.liquipedia_rating), liquipediaRating)) }
        team.sponsors?.let { sponsors -> if (sponsors.isNotEmpty()) items.add(KeyValueListItemDescriptor(context?.getString(R.string.sponsors), sponsors.joinToString(", "))) }
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

    private fun generateTeamSocialItems(team: Team): List<SocialType> {
        val items = mutableListOf<SocialType>()
        team.youtube?.let { items.add(SocialType.YOUTUBE) }
        team.twitch?.let { items.add(SocialType.TWITCH) }
        team.twitter?.let { items.add(SocialType.TWITTER) }
        team.discord?.let { items.add(SocialType.DISCORD) }
        team.instagram?.let { items.add(SocialType.INSTAGRAM) }
        team.facebook?.let { items.add(SocialType.FACEBOOK) }
        team.steam?.let { items.add(SocialType.STEAM) }
        team.snapchat?.let { items.add(SocialType.SNAPCHAT) }
        return items
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class TeamFragmentViewState(
        val isLoading: Boolean = false,
        val barTitle: String? = null,
        val teamGeneralDetailItems: List<KeyValueListItemDescriptor>? = null,
        val teamRosterPlayerItems: List<TeamRosterPlayerListItemDescriptor>? = null,
        val teamSocialItems: List<SocialType>? = null,
        val teamImages: List<String>? = null,
        val teamDescription: String? = null
    )

    sealed class TeamFragmentViewEffect {
        data class ShowError(val error: String): TeamFragmentViewEffect()
        data class PresentPersonFragment(val person: Person): TeamFragmentViewEffect()
        data class OpenActivity(val intent: Intent): TeamFragmentViewEffect()
    }

    sealed class TeamFragmentEvent {
        data class ViewCreated(val team: Team?, val context: Context?): TeamFragmentEvent()
        data class SelectedRosterItem(val item: TeamRosterPlayerListItemDescriptor): TeamFragmentEvent()
        data class SelectedTeamSocial(val socialType: SocialType): TeamFragmentEvent()
    }
}