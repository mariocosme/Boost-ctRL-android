package pt.cosmik.boostctrl.ui.competitions.detail

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.*
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class CompetitionViewModel: ViewModel() {

    val viewState = MutableLiveData(CompetitionFragmentViewState())
    val viewEffect = SingleLiveEvent<CompetitionFragmentViewEffect>()

    private val disposables = CompositeDisposable()
    private var competition: Competition? = null
    private var context: Context? = null

    fun processEvent(event: CompetitionFragmentEvent) {
        when (event) {
            is CompetitionFragmentEvent.ViewCreated -> {
                context = event.context

                if (event.competition == null) {
                    viewEffect.value = CompetitionFragmentViewEffect.ShowError("Something went wrong trying to present the chosen competition.")
                }
                else {
                    competition = event.competition
                    viewState.value = viewState.value?.copy(
                        barTitle = event.competition.nameAbbreviated,
                        competitionImage = event.competition.image,
                        competitionDescription = event.competition.description,
                        competitionGeneralDetailItems = generateCompetitionGeneralDetailItemDescriptors(event.competition)
                        //competitionRankingItems = generateTeamRosterPlayerItemDescriptors(event.competition)
                    )
                }
            }
            /*is CompetitionFragmentEvent.SelectedTeamItem -> {
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
            }*/
        }
    }

    private fun generateCompetitionGeneralDetailItemDescriptors(competition: Competition): List<CompetitionGeneralDetailListItemDescriptor> {
        val items = mutableListOf<CompetitionGeneralDetailListItemDescriptor>()
        items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.name), competition.name))
        competition.series?.let { series -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.series), series, competition.seriesIcon)) }
        competition.organizer?.let { organizer -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.organizer), organizer)) }
        competition.location?.let { location -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.location), location, competition.locationIcon)) }
        competition.sponsors?.let { sponsors -> if (sponsors.isNotEmpty()) items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.sponsors), sponsors.joinToString(", "))) }
        competition.competitionType?.let { competitionType -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.competition_type), competitionType)) }
        competition.venue?.let { venue -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.venue), venue)) }
        competition.startDate?.let { startDate -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.start_date), startDate)) }
        competition.endDate?.let { endDate -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.end_date), endDate)) }
        competition.prizePool?.let { prizePool -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.prize_pool), prizePool)) }
        competition.type?.let { type ->
            val typeStr = when (type) {
                CompetitionType.PREMIER -> context?.getString(R.string.premier)
                CompetitionType.MAJOR -> context?.getString(R.string.major)
                CompetitionType.MINOR -> context?.getString(R.string.minor)
                CompetitionType.OTHER -> context?.getString(R.string.other)
            }
            items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.competition_type), typeStr))
        }
        competition.numberOfTeams?.let { numberOfTeams -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.number_of_teams), numberOfTeams)) }
        competition.mode?.let { mode -> items.add(CompetitionGeneralDetailListItemDescriptor(context?.getString(R.string.mode), mode)) }
        return items
    }

    /*@SuppressLint("DefaultLocale")
    private fun generateTeamRosterPlayerItemDescriptors(competition: Competition): List<TeamRosterPlayerListItemDescriptor> {
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
    }*/

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class CompetitionFragmentViewState(
        val isLoading: Boolean = false,
        val barTitle: String? = null,
        val competitionGeneralDetailItems: List<CompetitionGeneralDetailListItemDescriptor>? = null,
        //val competitionRankingItems: List<TeamRosterPlayerListItemDescriptor>? = null,
        val competitionImage: String? = null,
        val competitionDescription: String? = null
    )

    sealed class CompetitionFragmentViewEffect {
        data class ShowError(val error: String): CompetitionFragmentViewEffect()
        data class PresentTeamFragment(val team: Team): CompetitionFragmentViewEffect()
    }

    sealed class CompetitionFragmentEvent {
        data class ViewCreated(val competition: Competition?, val context: Context?): CompetitionFragmentEvent()
        //data class SelectedTeamItem(val item: TeamListItemDescriptor): CompetitionFragmentEvent()
    }
}