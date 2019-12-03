package pt.cosmik.boostctrl.ui.matches.detail

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.ui.common.KeyValueActionListItemDescriptor
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
                        barTitle = getActionBarTitle(event.match),
                        homeTeamName = event.match.homeTeam?.name,
                        homeTeamImage = event.match.homeTeam?.mainImage,
                        awayTeamImage = event.match.awayTeam?.mainImage,
                        awayTeamName = event.match.awayTeam?.name,
                        matchDetailsItems = generateMatchDetailItems(event.match),
                        teamRostersItems = generateMatchTeamRosterItems(event.match),
                        matchActions = generateMatchActions(),
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
            is MatchFragmentEvent.SelectedAction -> {
                context?.let { context ->
                    when (event.actionDescriptor.title) {
                        context.getString(R.string.add_to_calendar) -> {
                            viewEffect.value = MatchFragmentViewEffect.StartActivity(
                                Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, match?.dateTime?.time)
                                    .putExtra(CalendarContract.Events.TITLE, getActionBarTitle(match))
                                    .putExtra(CalendarContract.Events.DESCRIPTION, match?.tournamentName)
                            )
                        }
                        context.getString(R.string.notify_me) -> {
                            viewEffect.value = MatchFragmentViewEffect.ShowError("Coming soon!")
                            // TODO: schedule notification implementation
                            /*val intent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                            val builder = NotificationCompat.Builder(context, context.getString(R.string.in_app_channel_id))
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setContentTitle("My notification")
                                .setContentText("Much longer text that cannot fit one line...")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setVisibility(VISIBILITY_PUBLIC)
                                .setAutoCancel(true)*/
                        }
                    }
                }
            }
        }
    }

    private fun reloadMatch() {
        match?.let { currentMatch ->
            viewState.value = viewState.value?.copy(isLoading = true)
            disposables.add(boostCtrlRepository.getUpcomingAndOngoingMatch(currentMatch.id).subscribe ({
                match = it
                viewState.value = viewState.value?.copy(
                    barTitle = getActionBarTitle(it),
                    homeTeamName = it.homeTeam?.name,
                    homeTeamImage = it.homeTeam?.mainImage,
                    awayTeamImage = it.awayTeam?.mainImage,
                    awayTeamName = it.awayTeam?.name,
                    matchDetailsItems = generateMatchDetailItems(it),
                    teamRostersItems = generateMatchTeamRosterItems(it),
                    matchActions = generateMatchActions(),
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

    private fun generateMatchActions(): List<KeyValueActionListItemDescriptor> = listOf(
        KeyValueActionListItemDescriptor(context?.getString(R.string.add_to_calendar), R.drawable.ic_calendar),
        KeyValueActionListItemDescriptor(context?.getString(R.string.notify_me), R.drawable.ic_alarm_add)
    )

    private fun getActionBarTitle(match: UpcomingMatch?): String = "${match?.homeTeam?.name} vs. ${match?.awayTeam?.name}"

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
        val teamRostersItems: List<MatchTeamRosterItemDescriptor>? = null,
        val matchActions: List<KeyValueActionListItemDescriptor>? = null
    )

    sealed class MatchFragmentViewEffect {
        data class ShowError(val error: String): MatchFragmentViewEffect()
        data class PresentPersonFragment(val person: Person): MatchFragmentViewEffect()
        data class StartActivity(val intent: Intent): MatchFragmentViewEffect()
    }

    sealed class MatchFragmentEvent {
        data class ViewCreated(val match: UpcomingMatch?, val context: Context?): MatchFragmentEvent()
        data class SelectedPlayer(val playerNickname: String): MatchFragmentEvent()
        object DidTriggerRefresh: MatchFragmentEvent()
        data class SelectedAction(val actionDescriptor: KeyValueActionListItemDescriptor): MatchFragmentEvent()
    }
}