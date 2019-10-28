package pt.cosmik.boostctrl.services

import io.reactivex.Observable
import pt.cosmik.boostctrl.models.NewsDataContainer
import retrofit2.http.GET
import retrofit2.http.Url

interface OctaneggService {

    @GET
    fun getLatestNews(@Url url: String): Observable<NewsDataContainer>

    @GET
    fun getNews(@Url url: String): Observable<NewsDataContainer>

}