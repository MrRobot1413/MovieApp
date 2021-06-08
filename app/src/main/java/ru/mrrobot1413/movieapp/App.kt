package ru.mrrobot1413.movieapp

import android.app.Application
import ru.mrrobot1413.movieapp.di.AppComponent
import ru.mrrobot1413.movieapp.di.AppComponentSource
import ru.mrrobot1413.movieapp.di.DaggerAppComponent
import ru.mrrobot1413.movieapp.di.modules.*
import ru.mrrobot1413.movieapp.ui.MainActivity

class App : Application() {

    private val appComponent: AppComponent = DaggerAppComponent
        .builder()
        .appModule(AppModule(this))
        .networkModule(NetworkModule())
        .roomModule(RoomModule(this))
        .repositoriesModule(RepositoriesModule())
        .build()

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

        AppComponentSource.setAppComponent(appComponent)
    }
}