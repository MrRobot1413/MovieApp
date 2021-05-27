package ru.mrrobot1413.movieapp.di.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mrrobot1413.movieapp.App
import ru.mrrobot1413.movieapp.BuildConfig
import ru.mrrobot1413.movieapp.api.Api
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->

                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder().addQueryParameter("api_key", App.API_KEY).build()
                request.url(url)
                return@addNetworkInterceptor chain.proceed(request.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(App.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun providesApi(): Api {
        return providesRetrofitBuilder().create(Api::class.java)
    }
}