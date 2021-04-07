package ru.mrrobot1413.movieapp.repositories

import retrofit2.Call
import ru.mrrobot1413.movieapp.App
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieResponse
import java.util.*


object MovieRepository {
    private lateinit var instance: MovieRepository
    val app: App = App()

    fun getInstance(): MovieRepository {
        instance = MovieRepository
        app.initRetrofit()
        return instance
    }

    fun getPopularMovies(
        page: Int
    ): Call<MovieResponse>{
        return app.api.getPopularMovies(page = page, language = Locale.getDefault().language)
    }

    fun getMovieDetails(
        id: Int
    ): Call<Movie>{
        return app.api.getMovieDetails(id = id, language = Locale.getDefault().language)
    }

    fun searchMovie(
        page: Int,
        query: String
    ): Call<MovieResponse>{
        return app.api.searchMovie(page = page, language = Locale.getDefault().language, query = query)
    }

    fun getTopRatedMovies(
        page: Int = 1
    ): Call<MovieResponse>{
        return app.api.getTopRatedMovies(page = page, language = Locale.getDefault().language)
    }
}
