package pt.cosmik.boostctrl.ui.teams.detail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class TeamViewModel: ViewModel() {

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
                        teamDetailItems = generateTeamDetailItemDescriptors(event.team)
                    )
                }
            }
        }
    }

    private fun generateTeamDetailItemDescriptors(team: Team): List<TeamDetailListItemDescriptor> {
        val items = mutableListOf<TeamDetailListItemDescriptor>()
        items.add(TeamDetailListItemDescriptor(context?.getString(R.string.name), team.name))
        team.sponsors?.let { sponsors -> if (sponsors.isNotEmpty()) items.add(TeamDetailListItemDescriptor(context?.getString(R.string.sponsors), sponsors.joinToString(", "))) }
        team.createdAt?.let { createdAt -> items.add(TeamDetailListItemDescriptor(context?.getString(R.string.created_at), createdAt)) }
        team.region?.let { region -> items.add(TeamDetailListItemDescriptor(context?.getString(R.string.region), region.toString())) }
        team.location?.let { location -> items.add(TeamDetailListItemDescriptor(context?.getString(R.string.location), location)) }
        team.roster?.let { roster -> items.add(TeamDetailListItemDescriptor(context?.getString(R.string.roster), roster.map { it.nickname }.joinToString(", "))) }
        team.manager?.let { manager -> items.add(TeamDetailListItemDescriptor(context?.getString(R.string.manager), manager)) }
        return items
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class TeamFragmentViewState(
        val barTitle: String? = null,
        val teamDetailItems: List<TeamDetailListItemDescriptor>? = null,
        val teamImages: List<String>? = null,
        val teamDescription: String? = null
    )

    sealed class TeamFragmentViewEffect {
        data class ShowError(val message: String): TeamFragmentViewEffect()
    }

    sealed class TeamFragmentEvent {
        data class ViewCreated(val team: Team?, val context: Context?): TeamFragmentEvent()
    }
}