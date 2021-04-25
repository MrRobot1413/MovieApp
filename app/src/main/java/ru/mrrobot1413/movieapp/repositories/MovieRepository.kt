package ru.mrrobot1413.movieapp.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mrrobot1413.movieapp.App
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

    suspend fun getPopularMovies(
        page: Int,
    ): MovieResponse {
        return withContext(Dispatchers.IO) {
            app.api.getPopularMovies(page = page, language = Locale.getDefault().language)
        }
    }

    suspend fun getTopRatedMovies(
        page: Int,
    ): MovieResponse {
        return withContext(Dispatchers.IO) {
            app.api.getTopRatedMovies(page = page,
                language = Locale.getDefault().language)
        }
    }

    suspend fun getMovieDetails(
        id: Int,
    ): MovieNetwork {
        return withContext(Dispatchers.IO) {
            app.api.getMovieDetails(id = id,
                language = Locale.getDefault().language)
        }
    }

    suspend fun searchMovie(
        page: Int,
        query: String,
    ): MovieResponse {
        return withContext(Dispatchers.IO) {
            app.api.searchMovie(page = page,
                language = Locale.getDefault().language,
                query = query)
        }
    }

    suspend fun getVideos(
        id: Int,
    ): VideoResponse {
        return withContext(Dispatchers.IO) {
            app.api.getVideos(id,
                language = Locale.getDefault().language)
        }
    }

    suspend fun searchMovieByGenre(
        genreId: Int,
    ): MovieResponse {
        return withContext(Dispatchers.IO) {
            app.api.searchByGenre(id = genreId,
                language = Locale.getDefault().language)
        }
    }
}
