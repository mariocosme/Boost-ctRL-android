package pt.cosmik.boostctrl.ui.person

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.LocalDate
import org.joda.time.Years
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.ui.common.KeyValueListItemDescriptor
import pt.cosmik.boostctrl.ui.common.SocialType
import pt.cosmik.boostctrl.utils.SingleLiveEvent
import java.text.DateFormat


class PersonViewModel: ViewModel() {

    val viewState = MutableLiveData(PersonFragmentViewState())
    val viewEffect = SingleLiveEvent<PersonFragmentViewEffect>()

    private val disposables = CompositeDisposable()
    private var person: Person? = null
    private var context: Context? = null

    fun processEvent(event: PersonFragmentEvent) {
        when (event) {
            is PersonFragmentEvent.ViewCreated -> {
                context = event.context

                if (event.person == null) {
                    viewEffect.value = PersonFragmentViewEffect.ShowError("Something went wrong trying to present the chosen person.")
                }
                else {
                    person = event.person
                    viewState.value = viewState.value?.copy(
                        barTitle = event.person.nickname,
                        personImages = event.person.images,
                        personDescription = event.person.summary,
                        personDetailItems = generatePersonDetailItemDescriptors(event.person),
                        personSocialItems = generatePersonSocialItems(event.person)
                    )
                }
            }
            is PersonFragmentEvent.SelectedPersonSocial -> {
                val intent = when (event.socialType) {
                    SocialType.DISCORD -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.discord))
                    SocialType.INSTAGRAM -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.instagram))
                    SocialType.TWITTER -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.twitter))
                    SocialType.TWITCH -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.twitch))
                    SocialType.FACEBOOK -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.facebook))
                    SocialType.YOUTUBE -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.youtube))
                    SocialType.STEAM -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.steam))
                    SocialType.SNAPCHAT -> Intent(Intent.ACTION_VIEW, Uri.parse(person?.snapchat))
                }
                viewEffect.value = PersonFragmentViewEffect.OpenActivity(intent)
            }
        }
    }

    private fun generatePersonDetailItemDescriptors(person: Person): List<KeyValueListItemDescriptor> {
        val items = mutableListOf<KeyValueListItemDescriptor>()
        person.name?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.name), it)) }
        person.nickname?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.nickname), it)) }
        person.birthDate?.let {
            val age: Years = Years.yearsBetween(LocalDate(it), LocalDate())
            val ageString = context?.getString(R.string.age, age.years.toString())
            items.add(KeyValueListItemDescriptor(context?.getString(R.string.birthdate), "${DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)} ($ageString)"))
        }
        person.country?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.country), it, person.countryIcon)) }
        person.currentTeam?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.current_team), it)) }
        person.role?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.role), it as String?)) }
        person.status?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.status), it)) }
        person.approxTotalEarnings?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.approximate_total_earnings), it)) }
        person.startingGame?.let { items.add(KeyValueListItemDescriptor(context?.getString(R.string.starting_game), it)) }
        return items
    }

    private fun generatePersonSocialItems(person: Person): List<SocialType> {
        val items = mutableListOf<SocialType>()
        person.youtube?.let { items.add(SocialType.YOUTUBE) }
        person.twitch?.let { items.add(SocialType.TWITCH) }
        person.twitter?.let { items.add(SocialType.TWITTER) }
        person.discord?.let { items.add(SocialType.DISCORD) }
        person.instagram?.let { items.add(SocialType.INSTAGRAM) }
        person.facebook?.let { items.add(SocialType.FACEBOOK) }
        person.steam?.let { items.add(SocialType.STEAM) }
        person.snapchat?.let { items.add(SocialType.SNAPCHAT) }
        return items
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class PersonFragmentViewState(
        val barTitle: String? = null,
        val personDetailItems: List<KeyValueListItemDescriptor>? = null,
        val personImages: List<String>? = null,
        val personDescription: String? = null,
        val personSocialItems: List<SocialType>? = null
    )

    sealed class PersonFragmentViewEffect {
        data class ShowError(val error: String): PersonFragmentViewEffect()
        data class OpenActivity(val intent: Intent): PersonFragmentViewEffect()
    }

    sealed class PersonFragmentEvent {
        data class ViewCreated(val person: Person?, val context: Context?): PersonFragmentEvent()
        data class SelectedPersonSocial(val socialType: SocialType): PersonFragmentEvent()
    }
}