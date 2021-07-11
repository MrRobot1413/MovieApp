package ru.mrrobot1413.movieapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mrrobot1413.movieapp.api.Api
import ru.mrrobot1413.movieapp.dao.MovieDao
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun providesMovieRepository(api: Api): MovieRepository {
        return MovieRepository(api)
    }

    @Singleton
    @Provides
    fun providesDbRepository(movieDao: MovieDao): DbListRepository {
        return DbListRepository(movieDao)
    }
}