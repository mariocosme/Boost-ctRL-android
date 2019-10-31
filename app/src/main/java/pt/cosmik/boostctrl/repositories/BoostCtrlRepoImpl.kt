package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pt.cosmik.boostctrl.BuildConfig
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.TournamentRanking
import pt.cosmik.boostctrl.models.UpdateTimeKind
import pt.cosmik.boostctrl.models.UpdatedTime
import pt.cosmik.boostctrl.services.BoostCtrlService
import pt.cosmik.boostctrl.utils.Constants
import pt.cosmik.boostctrl.utils.HashUtils

class BoostCtrlRepoImpl(private val boostCtrlService: BoostCtrlService): BoostCtrlRepository {

    override fun getPerson(personName: String): Observable<Person?> {
        val hash = HashUtils.sha1("GET/person/$personName${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/person/$personName?hash=$hash"
        return boostCtrlService.getPerson(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getRankings(): Observable<List<TournamentRanking>> {
        val hash = HashUtils.sha1("GET/rankings${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/rankings?hash=$hash"
        return boostCtrlService.getRankings(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUpdatedTime(kind: UpdateTimeKind): Observable<UpdatedTime> {
        val hash = HashUtils.sha1("GET/updated-time${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/updated-time?hash=$hash"
        return boostCtrlService.getUpdatedTime(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}