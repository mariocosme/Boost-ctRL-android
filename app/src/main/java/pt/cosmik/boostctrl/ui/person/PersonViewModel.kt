package pt.cosmik.boostctrl.ui.person

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.LocalDate
import org.joda.time.Years
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.ui.common.KeyValueListItemDescriptor
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
                        personDetailItems = generatePersonDetailItemDescriptors(event.person)
                    )
                }
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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class PersonFragmentViewState(
        val barTitle: String? = null,
        val personDetailItems: List<KeyValueListItemDescriptor>? = null,
        val personImages: List<String>? = null,
        val personDescription: String? = null
    )

    sealed class PersonFragmentViewEffect {
        data class ShowError(val message: String): PersonFragmentViewEffect()
    }

    sealed class PersonFragmentEvent {
        data class ViewCreated(val person: Person?, val context: Context?): PersonFragmentEvent()
    }
}