package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.*

interface BoostCtrlRepository {

    fun getPerson(personName: String): Observable<Person?>

    fun getAllCompetitions(): Observable<List<Competition>>
    fun getCompetition(competitionId: String): Observable<Competition>

    fun getUpcomingAndOngoingMatches(): Observable<List<UpcomingMatch>>

    fun getActiveTeams(): Observable<List<Team>>
    fun getTeam(teamName: String): Observable<Team?>

    fun getNews(page: Int): Observable<List<NewsItem>>
    fun getNewsItem(newsItemId: String): Observable<NewsItem>
}