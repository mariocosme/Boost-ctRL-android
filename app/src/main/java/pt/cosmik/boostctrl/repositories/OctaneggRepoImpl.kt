package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.services.OctaneggService
import pt.cosmik.boostctrl.utils.Constants

class OctaneggRepoImpl(private val octaneggService: OctaneggService): OctaneggRepository {

    override fun getLatestNews(): Observable<List<NewsItem>> {
        return octaneggService.getLatestNews("${Constants.OCTANE_GG_API}/news")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.data }
    }

    override fun getNews(month: String, year: String): Observable<List<NewsItem>> {
        return octaneggService.getNews("${Constants.OCTANE_GG_API}/news/$year/$month")
            .subscribeOn(AndroidSchedulers.mainThread()).map { it.data }
    }

}