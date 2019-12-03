package pt.cosmik.boostctrl.ui.competitions.detail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.BracketContainer
import pt.cosmik.boostctrl.models.Competition
import pt.cosmik.boostctrl.models.CompetitionType
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.ui.common.KeyValueListItemDescriptor
import pt.cosmik.boostctrl.utils.SingleLiveEvent

class CompetitionViewModel(private val boostCtrlRepository: BoostCtrlRepository): ViewModel() {

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
                        competitionGeneralDetailItems = generateCompetitionGeneralDetailItemDescriptors(event.competition),
                        competitionStandingItems = generateStandingItemDescriptors(event.competition),
                        competitionBrackets = event.competition.brackets
                    )
                }
            }
            is CompetitionFragmentEvent.SelectedTeamItem -> {
                event.item.teamName?.let { team ->
                    viewState.value = viewState.value?.copy(isLoading = true)

                    disposables.add(boostCtrlRepository.getTeam(team).subscribe ({
                        viewState.value = viewState.value?.copy(isLoading = false)
                        it?.let { team -> viewEffect.value = CompetitionFragmentViewEffect.PresentTeamFragment(team) }
                    }, {
                        Crashlytics.logException(it)
                        viewState.value = viewState.value?.copy(isLoading = false)
                        viewEffect.value = CompetitionFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected team.")
                    }))
                }
            }
        }
    }

    private fun generateCompetitionGeneralDetailItemDescriptors(competition: Competition): List<KeyValueListItemDescriptor> {
        val items = mutableListOf<KeyValueListItemDescriptor>()
        items.add(KeyValueListItemDescriptor(context?.getString(R.string.name), competition.name))
        competition.series?.let { series -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.series), series, competition.seriesIcon)) }
        competition.organizer?.let { organizer -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.organizer), organizer)) }
        competition.location?.let { location -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.location), location, competition.locationIcon)) }
        competition.sponsors?.let { sponsors -> if (sponsors.isNotEmpty()) items.add(KeyValueListItemDescriptor(context?.getString(R.string.sponsors), sponsors.joinToString(", "))) }
        competition.competitionType?.let { competitionType -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.competition_type), competitionType)) }
        competition.venue?.let { venue -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.venue), venue)) }
        competition.startDate?.let { startDate -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.start_date), startDate)) }
        competition.endDate?.let { endDate -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.end_date), endDate)) }
        competition.prizePool?.let { prizePool -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.prize_pool), prizePool)) }
        competition.type?.let { type ->
            val typeStr = when (type) {
                CompetitionType.PREMIER -> context?.getString(R.string.premier)
                CompetitionType.MAJOR -> context?.getString(R.string.major)
                CompetitionType.MINOR -> context?.getString(R.string.minor)
                CompetitionType.OTHER -> context?.getString(R.string.other)
            }
            items.add(KeyValueListItemDescriptor(context?.getString(R.string.competition_type), typeStr))
        }
        competition.numberOfTeams?.let { numberOfTeams -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.number_of_teams), numberOfTeams)) }
        competition.mode?.let { mode -> items.add(KeyValueListItemDescriptor(context?.getString(R.string.mode), mode)) }
        return items
    }

    private fun generateStandingItemDescriptors(competition: Competition): List<StandingItemDescriptor> {
        val items = mutableListOf<StandingItemDescriptor>()
        competition.groupStandings?.teams?.forEachIndexed { index, team ->
            val item = StandingItemDescriptor()
            item.index = "${index+1}."
            item.teamName = team
            item.teamImage = competition.groupStandings.teamImages?.get(index)
            item.seriesWL = competition.groupStandings.winLoss?.get(index)
            item.gamesDifference = competition.groupStandings.gamesDifference?.get(index)
            item.gamesWL = competition.groupStandings.gamesWinLoss?.get(index)
            item.teamColor = competition.groupStandings.colors?.get(index)
            items.add(item)
        }
        return items
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class CompetitionFragmentViewState(
        val isLoading: Boolean = false,
        val barTitle: String? = null,
        val competitionGeneralDetailItems: List<KeyValueListItemDescriptor>? = null,
        val competitionStandingItems: List<StandingItemDescriptor>? = null,
        val competitionImage: String? = null,
        val competitionDescription: String? = null,
        val competitionBrackets: List<BracketContainer>? = null
    )

    sealed class CompetitionFragmentViewEffect {
        data class ShowError(val error: String): CompetitionFragmentViewEffect()
        data class PresentTeamFragment(val team: Team): CompetitionFragmentViewEffect()
    }

    sealed class CompetitionFragmentEvent {
        data class ViewCreated(val competition: Competition?, val context: Context?): CompetitionFragmentEvent()
        data class SelectedTeamItem(val item: StandingItemDescriptor): CompetitionFragmentEvent()
    }
}