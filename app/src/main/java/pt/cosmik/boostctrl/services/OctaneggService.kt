package pt.cosmik.boostctrl.services

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.NewsDataContainer
import retrofit2.http.GET
import retrofit2.http.Path

interface OctaneggService {

    @GET("news")
    fun getLatestNews(): Observable<NewsDataContainer>

    @GET("news/{year}/{month}")
    fun getNews(@Path("month") month: String, @Path("year") year: String): Observable<NewsDataContainer>

}