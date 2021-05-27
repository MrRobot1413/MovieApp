package ru.mrrobot1413.movieapp.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(app: Application) {
    private val application = app

    @Singleton
    @Provides
    fun providesApplication(): Application {
        return application
    }
}