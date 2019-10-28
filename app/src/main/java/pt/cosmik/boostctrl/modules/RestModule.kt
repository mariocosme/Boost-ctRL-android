package pt.cosmik.boostctrl.modules

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pt.cosmik.boostctrl.services.BoostCtrlService
import pt.cosmik.boostctrl.services.OctaneggService
import pt.cosmik.boostctrl.utils.BoostCtrlInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

var restModule = module {

    single { Gson() }
    single { HttpLoggingInterceptor() }
    single { BoostCtrlInterceptor() }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<BoostCtrlInterceptor>())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api/") // We use the @Url param because we use more than one base URL
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create(get<Gson>()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single<OctaneggService> { get<Retrofit>().create(OctaneggService::class.java) }

    single<BoostCtrlService> { get<Retrofit>().create(BoostCtrlService::class.java) }

}