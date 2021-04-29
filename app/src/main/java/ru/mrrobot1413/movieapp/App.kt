package ru.mrrobot1413.movieapp

import android.app.Application
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mrrobot1413.movieapp.api.Api

class App : Application() {

    lateinit var api: Api
    lateinit var db: AppDatabase

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "82f337a96c72f107c937a1fcf9d4072c"

        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "movies"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    fun initRetrofit() {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->

                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder().addQueryParameter("api_key", API_KEY).build()
                request.url(url)
                return@addNetworkInterceptor chain.proceed(request.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getInstance(): App {
        return instance
    }

    fun getDatabase(): AppDatabase {
        return db
    }
}