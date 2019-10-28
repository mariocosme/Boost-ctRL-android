package pt.cosmik.boostctrl.services

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.Person
import retrofit2.http.GET
import retrofit2.http.Url

interface BoostCtrlService {

    @GET
    fun getPerson(@Url url: String): Observable<Person>

}