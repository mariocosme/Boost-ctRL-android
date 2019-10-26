package pt.cosmik.boostctrl.modules

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pt.cosmik.boostctrl.services.OctaneggService
import pt.cosmik.boostctrl.utils.BoostCtrlInterceptor
import pt.cosmik.boostctrl.utils.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

var restModule = module {

    single { Gson() }

    single {
        HttpLoggingInterceptor()
    }

    single {
        BoostCtrlInterceptor()
    }

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
        val gson = get<Gson>()
        Retrofit.Builder()
            .baseUrl(Constants.OCTANE_GG_API)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single<OctaneggService> { get<Retrofit>().create(OctaneggService::class.java) }

}