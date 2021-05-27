package ru.mrrobot1413.movieapp.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.mrrobot1413.movieapp.App
import ru.mrrobot1413.movieapp.AppDatabase
import ru.mrrobot1413.movieapp.dao.MovieDao
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            App.TABLE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesRoomDatabase(): AppDatabase{
        return db
    }

    @Singleton
    @Provides
    fun providesDao(): MovieDao {
        return providesRoomDatabase().movieDao()
    }
}