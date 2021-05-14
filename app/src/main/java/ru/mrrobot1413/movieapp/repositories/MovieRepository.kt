package ru.mrrobot1413.movieapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.mrrobot1413.movieapp.App
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.model.VideoResponse
import ru.mrrobot1413.movieapp.ui.MoviePagingSource
import java.util.*

object MovieRepository {
    private lateinit var instance: MovieRepository
    val app: App = App()

    const val REQUEST_TYPE_POPULAR = "popular"
    const val REQUEST_TYPE_TOP_RATED = "top_rated"
    const val REQUEST_TYPE_SEARCH = "search"
    const val REQUEST_TYPE_GENRE_SEARCH = "genre_search"

    fun getInstance(): MovieRepository {
        instance = MovieRepository
        app.initRetrofit()
        return instance
    }

    fun getPopularMovies(): Flow<PagingData<MovieNetwork>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                initialLoadSize = 60,
            ), pagingSourceFactory = {
                MoviePagingSource(app.api, "", null, REQUEST_TYPE_POPULAR)
            }).flow
    }

    fun getTopRatedMovies(): Flow<PagingData<MovieNetwork>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                initialLoadSize = 60,
                enablePlaceholders = true
            ), pagingSourceFactory = {
                MoviePagingSource(app.api, "", null, REQUEST_TYPE_TOP_RATED)
            }).flow
    }

    suspend fun getMovieDetails(
        id: Int,
    ): MovieNetwork {
        return withContext(Dispatchers.IO) {
            app.api.getMovieDetails(id = id,
                language = Locale.getDefault().language)
        }
    }

    fun searchMovie(
        query: String,
    ): Flow<PagingData<MovieNetwork>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                initialLoadSize = 60,
                enablePlaceholders = true
            ), pagingSourceFactory = {
                MoviePagingSource(app.api, query, null, REQUEST_TYPE_SEARCH)
            }).flow
    }

    suspend fun getVideos(
        id: Int,
    ): VideoResponse {
        return withContext(Dispatchers.IO) {
            app.api.getVideos(id,
                language = Locale.getDefault().language)
        }
    }

    fun searchMovieByGenre(
        genreId: Int,
    ): Flow<PagingData<MovieNetwork>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                initialLoadSize = 60,
                enablePlaceholders = true
            ), pagingSourceFactory = {
                MoviePagingSource(app.api, "", genreId, REQUEST_TYPE_GENRE_SEARCH)
            }).flow
    }
}
