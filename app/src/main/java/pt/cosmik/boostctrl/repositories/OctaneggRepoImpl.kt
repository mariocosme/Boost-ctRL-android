package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.services.OctaneggService

class OctaneggRepoImpl(private val octaneggService: OctaneggService): OctaneggRepository {

    override fun getLatestNews(): Observable<List<NewsItem>> {
        return octaneggService.getLatestNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.data }
    }

    override fun getNews(month: String, year: String): Observable<List<NewsItem>> {
        return octaneggService.getNews(month, year).subscribeOn(AndroidSchedulers.mainThread()).map { it.data }
    }

}