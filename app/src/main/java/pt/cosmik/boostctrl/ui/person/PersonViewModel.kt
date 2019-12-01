package pt.cosmik.boostctrl.ui.person

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
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

    private fun generatePersonDetailItemDescriptors(person: Person): List<PersonDetailListItemDescriptor> {
        val items = mutableListOf<PersonDetailListItemDescriptor>()
        person.name?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.name), it)) }
        person.nickname?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.nickname), it)) }
        person.birthDate?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.birthdate), DateFormat.getDateInstance(DateFormat.SHORT).format(it))) }
        person.country?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.country), it, person.countryIcon)) }
        person.currentTeam?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.current_team), it)) }
        person.role?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.role), it as String?)) }
        person.status?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.status), it)) }
        person.approxTotalEarnings?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.approximate_total_earnings), it)) }
        person.startingGame?.let { items.add(PersonDetailListItemDescriptor(context?.getString(R.string.starting_game), it)) }
        return items
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    data class PersonFragmentViewState(
        val barTitle: String? = null,
        val personDetailItems: List<PersonDetailListItemDescriptor>? = null,
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