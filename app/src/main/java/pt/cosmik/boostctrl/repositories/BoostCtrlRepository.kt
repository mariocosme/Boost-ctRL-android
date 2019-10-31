package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.TournamentRanking
import pt.cosmik.boostctrl.models.UpdateTimeKind
import pt.cosmik.boostctrl.models.UpdatedTime

interface BoostCtrlRepository {
    fun getPerson(personName: String): Observable<Person?>
    fun getRankings(): Observable<List<TournamentRanking>>
    fun getUpdatedTime(kind: UpdateTimeKind): Observable<UpdatedTime>
}