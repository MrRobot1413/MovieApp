package ru.mrrobot1413.movieapp.di.modules

import dagger.Module
import dagger.Provides
import ru.mrrobot1413.movieapp.api.Api
import ru.mrrobot1413.movieapp.dao.MovieDao
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import javax.inject.Singleton

@Module
class RepositoriesModule {

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