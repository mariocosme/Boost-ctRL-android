package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pt.cosmik.boostctrl.BuildConfig
import pt.cosmik.boostctrl.models.*
import pt.cosmik.boostctrl.services.BoostCtrlService
import pt.cosmik.boostctrl.utils.Constants
import pt.cosmik.boostctrl.utils.HashUtils

class BoostCtrlRepoImpl(private val boostCtrlService: BoostCtrlService): BoostCtrlRepository {

    override fun getPerson(personName: String): Observable<Person?> {
        val personParam = personName.replace(" ", "%20")
        val hash = HashUtils.sha1("GET/person/$personParam${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/person/$personParam?hash=$hash"
        return boostCtrlService.getPerson(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAllCompetitions(): Observable<List<Competition>> {
        val hash = HashUtils.sha1("GET/competition/all${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/competition/all?hash=$hash"
        return boostCtrlService.getAllCompetitions(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCompetition(competitionId: String): Observable<Competition> {
        val hash = HashUtils.sha1("GET/competition/$competitionId${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/competition/$competitionId?hash=$hash"
        return boostCtrlService.getCompetition(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCompetitionBrackets(competitionId: String): Observable<List<BracketContainer>> {
        val hash = HashUtils.sha1("GET/competition/$competitionId/brackets${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/competition/$competitionId/brackets?hash=$hash"
        return boostCtrlService.getCompetitionBrackets(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUpcomingAndOngoingMatches(): Observable<List<UpcomingMatch>> {
        val hash = HashUtils.sha1("GET/upcoming-matches${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/upcoming-matches?hash=$hash"
        return boostCtrlService.getUpcomingAndOngoingMatches(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUpcomingAndOngoingMatch(matchId: String): Observable<UpcomingMatch> {
        val hash = HashUtils.sha1("GET/upcoming-matches/$matchId${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/upcoming-matches/$matchId?hash=$hash"
        return boostCtrlService.getUpcomingAndOngoingMatch(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getActiveTeams(): Observable<List<Team>> {
        val hash = HashUtils.sha1("GET/team/all${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/team/all?hash=$hash"
        return boostCtrlService.getActiveTeams(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getTeam(teamName: String): Observable<Team?> {
        val teamParam = teamName.replace(" ", "%20")
        val hash = HashUtils.sha1("GET/team/$teamParam${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/team/$teamParam?hash=$hash"
        return boostCtrlService.getTeam(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getNews(page: Int): Observable<List<NewsItem>> {
        val hash = HashUtils.sha1("GET/news${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/news?page=$page&hash=$hash"
        return boostCtrlService.getNews(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getNewsItem(newsItemId: String): Observable<NewsItem> {
        val hash = HashUtils.sha1("GET/news/$newsItemId${BuildConfig.BOOST_CTRL_API_SECRET}")
        val url = "${Constants.BOOST_CTRL_API}/news/$newsItemId?hash=$hash"
        return boostCtrlService.getNewsItem(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}