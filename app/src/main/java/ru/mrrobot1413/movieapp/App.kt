package ru.mrrobot1413.movieapp

import android.app.Application
import ru.mrrobot1413.movieapp.di.modules.*
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "82f337a96c72f107c937a1fcf9d4072c"
        const val TABLE_NAME = "movies"

        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}