package ru.mrrobot1413.movieapp.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mrrobot1413.movieapp.api.Api
import ru.mrrobot1413.movieapp.di.AppComponentSource
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.model.VideoResponse
import java.util.*
import javax.inject.Inject

class MovieRepository @Inject constructor(apiSource: Api) {
    private val api: Api = apiSource

    init {
        AppComponentSource.appComponentSource.inject(this)
    }

    suspend fun getPopularMovies(page: Int): MovieResponse {
        return withContext(Dispatchers.IO){
            api.getPopularMovies(page, Locale.getDefault().language)
        }
    }

    suspend fun getTopRatedMovies(page: Int): MovieResponse {
        return withContext(Dispatchers.IO){
            api.getTopRatedMovies(page, Locale.getDefault().language)
        }
    }

    suspend fun getMovieDetails(
        id: Int,
    ): MovieNetwork {
        return withContext(Dispatchers.IO) {
            api.getMovieDetails(id = id,
                language = Locale.getDefault().language)
        }
    }

    suspend fun searchMovie(
        page: Int,
        query: String
    ): MovieResponse {
        return withContext(Dispatchers.IO){
            api.searchMovie(page, Locale.getDefault().language, query)
        }
    }

    suspend fun getVideos(
        id: Int,
    ): VideoResponse {
        return withContext(Dispatchers.IO) {
            api.getVideos(id,
                language = Locale.getDefault().language)
        }
    }

    suspend fun searchMovieByGenre(
        genreId: Int,
    ): MovieResponse {
        return  withContext(Dispatchers.IO){
            api.searchByGenre(genreId, Locale.getDefault().language)
        }
    }
}
