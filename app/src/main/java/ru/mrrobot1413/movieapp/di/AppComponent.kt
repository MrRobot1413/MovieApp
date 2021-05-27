package ru.mrrobot1413.movieapp.di

import android.app.Application
import dagger.Component
import retrofit2.Retrofit
import ru.mrrobot1413.movieapp.AppDatabase
import ru.mrrobot1413.movieapp.api.Api
import ru.mrrobot1413.movieapp.dao.MovieDao
import ru.mrrobot1413.movieapp.di.modules.AppModule
import ru.mrrobot1413.movieapp.di.modules.NetworkModule
import ru.mrrobot1413.movieapp.di.modules.RepositoriesModule
import ru.mrrobot1413.movieapp.di.modules.RoomModule
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import ru.mrrobot1413.movieapp.viewModels.FavoriteListViewModel
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RoomModule::class, RepositoriesModule::class])
interface AppComponent {

    fun inject(movieRepository: MovieRepository)

    fun inject(databaseRepository: DbListRepository)

    fun inject(moviesViewModel: MoviesViewModel)

    fun inject(favoriteListViewModel: FavoriteListViewModel)

    fun application(): Application

    fun retrofitBuilder(): Retrofit

    fun api(): Api

    fun roomDatabase(): AppDatabase

    fun dao(): MovieDao

    fun movieRepository(): MovieRepository

    fun dbRepository(): DbListRepository
}