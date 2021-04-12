package ru.mrrobot1413.movieapp

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.room.Room
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
            .build()
    }

    fun initRetrofit() {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                return@addInterceptor chain.proceed(
                    chain
                        .request()
                        .newBuilder()
                        .build()
                )
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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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