package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.*

interface BoostCtrlRepository {

    fun getPerson(personName: String): Observable<Person?>

    fun getRankings(): Observable<List<TournamentRanking>>

    fun getUpdatedTime(kind: UpdateTimeKind): Observable<UpdatedTime>

    fun getUpcomingAndOngoingMatches(): Observable<List<UpcomingMatch>>

    fun getActiveTeams(): Observable<List<Team>>
    fun getTeam(teamName: String): Observable<Team?>

    fun getNews(page: Int): Observable<List<NewsItem>>
    fun getNewsItem(newsItemId: String): Observable<NewsItem>
}