package pt.cosmik.boostctrl.repositories

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.NewsItem

interface OctaneggRepository {
    fun getLatestNews(): Observable<List<NewsItem>>
    fun getNews(month: String, year: String): Observable<List<NewsItem>>
}