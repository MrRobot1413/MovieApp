package ru.mrrobot1413.movieapp.repositories

import io.reactivex.Single
import retrofit2.Call
import ru.mrrobot1413.movieapp.App
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.model.VideoResponse
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
    ): Single<MovieResponse>{
        return app.api.getPopularMovies(page = page, language = Locale.getDefault().language)
    }

    fun getTopRatedMovies(
        page: Int = 1
    ): Single<MovieResponse>{
        return app.api.getTopRatedMovies(page = page, language = Locale.getDefault().language)
    }

    fun getMovieDetails(
        id: Int
    ): Single<MovieNetwork>{
        return app.api.getMovieDetails(id = id, language = Locale.getDefault().language)
    }

    fun searchMovie(
        page: Int,
        query: String
    ): Single<MovieResponse>{
        return app.api.searchMovie(page = page, language = Locale.getDefault().language, query = query)
    }

    fun getVideos(
        id: Int,
    ): Single<VideoResponse>{
        return app.api.getVideos(id, language = Locale.getDefault().language)
    }

    fun searchMovieByGenre(
        genreId: Int
    ): Single<MovieResponse>{
        return app.api.searchByGenre(id = genreId, language = Locale.getDefault().language)
    }
}
