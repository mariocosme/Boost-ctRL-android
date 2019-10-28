package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.Person

interface BoostCtrlRepository {
    fun getPerson(personName: String): Observable<Person?>
}