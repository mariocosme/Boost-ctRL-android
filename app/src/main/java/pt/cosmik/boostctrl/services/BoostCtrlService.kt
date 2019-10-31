package pt.cosmik.boostctrl.services

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.TournamentRanking
import pt.cosmik.boostctrl.models.UpdatedTime
import retrofit2.http.GET
import retrofit2.http.Url

interface BoostCtrlService {

    @GET
    fun getPerson(@Url url: String): Observable<Person>

    @GET
    fun getRankings(@Url url: String): Observable<List<TournamentRanking>>

    @GET
    fun getUpdatedTime(@Url url: String): Observable<UpdatedTime>

}