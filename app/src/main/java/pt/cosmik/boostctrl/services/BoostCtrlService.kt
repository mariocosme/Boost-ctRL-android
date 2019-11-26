package pt.cosmik.boostctrl.services

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.*
import retrofit2.http.GET
import retrofit2.http.Url

interface BoostCtrlService {

    @GET
    fun getPerson(@Url url: String): Observable<Person>

    @GET
    fun getRankings(@Url url: String): Observable<List<TournamentRanking>>

    @GET
    fun getUpdatedTime(@Url url: String): Observable<UpdatedTime>

    @GET
    fun getUpcomingAndOngoingMatches(@Url url: String): Observable<List<UpcomingMatch>>

    @GET
    fun getActiveTeams(@Url url: String): Observable<List<Team>>

    @GET
    fun getTeam(@Url url: String): Observable<Team>

    @GET
    fun getNews(@Url url: String): Observable<List<NewsItem>>
    @GET
    fun getNewsItem(@Url url: String): Observable<NewsItem>

}