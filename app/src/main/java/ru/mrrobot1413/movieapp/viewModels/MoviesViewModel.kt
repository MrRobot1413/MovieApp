package ru.mrrobot1413.movieapp.viewModels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mrrobot1413.movieapp.NotifyWorker
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.di.AppComponentSource.Companion.appComponentSource
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MoviesViewModel : ViewModel() {
    @Inject
    lateinit var movieRepository: MovieRepository
    @Inject
    lateinit var dbRepository: DbListRepository

    private val _movies = MutableStateFlow<List<MovieNetwork>>(mutableListOf())
    val movies: StateFlow<List<MovieNetwork>> = _movies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _movieDetailed = MutableStateFlow(MovieNetwork(
        0,
        "",
        "", "", 1.0f, "", 1, ""
    ))
    val movieDetailed: StateFlow<MovieNetwork?> = _movieDetailed

    private val _videoKey = MutableStateFlow("")
    val videoKey: StateFlow<String> = _videoKey

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible

    var pages = 0

    init {
        appComponentSource.inject(this)
    }

    fun getPopularMovies(
        page: Int,
        error: String
    ) {
        viewModelScope.launch {
            try {
                _isVisible.value = true
                _movies.value = movieRepository.getPopularMovies(page).moviesList
                _isVisible.value = false
            } catch (e: Exception) {
                _error.value = error
            }
        }
    }

    fun getTopRatedMovies(
        page: Int,
        noConnection: String
    ) {
        viewModelScope.launch {
            try {
                _isVisible.value = true
                _movies.value = movieRepository.getTopRatedMovies(page).moviesList
                _isVisible.value = false
            } catch (e: Exception) {
                _error.value = noConnection
            }
        }
    }

    fun getMovieDetails(
        id: Int,
    ) {
        viewModelScope.launch {
            try {
                Log.d("SUPERTAG", movieRepository.getMovieDetails(id = id).toString())
                _movieDetailed.value = movieRepository.getMovieDetails(id = id)
            } catch (e: Exception) {
                val movieFromDb: Movie? = dbRepository.selectById(id)
                if(movieFromDb != null) {
                    _movieDetailed.value = MovieNetwork(
                        movieFromDb.id,
                        movieFromDb.title,
                        movieFromDb.overview,
                        movieFromDb.posterPath,
                        movieFromDb.rating,
                        movieFromDb.releaseDate,
                        movieFromDb.time,
                        movieFromDb.language
                    )
                }
            }
        }
    }

    fun searchMovie(
        page: Int,
        query: String?,
        noConnection: String
    ) {
        viewModelScope.launch {
            if (query?.length!! >= 2) {
                try {
                    _isVisible.value = true
                    delay(300)
                    _movies.value = movieRepository.searchMovie(page, query).moviesList
                    _isVisible.value = false
                } catch (e: Exception) {
                    _error.value = noConnection
                }
            }
        }
    }

    fun getVideos(
        id: Int
    ) {
        viewModelScope.launch {
            try {
                val list = movieRepository.getVideos(id).list
                if (list.isNullOrEmpty()) {
                    _videoKey.value = "https://www.youtube.com/results?search_query=${_movieDetailed.value?.title + " " + _movieDetailed.value?.releaseDate} trailer"
                } else {
                    _videoKey.value = "http://www.youtube.com/watch?v=${
                        list[0].key
                    }"
                }
            } catch (e: Exception){
                _videoKey.value = "https://www.youtube.com/results?search_query=${_movieDetailed.value?.title + " " + _movieDetailed.value?.releaseDate} trailer"
            }
        }
    }

    fun searchMovieByGenre(
        genreId: Int,
        noConnection: String
    ) {
        viewModelScope.launch {
            try {
                _isVisible.value = true
                _movies.value = movieRepository.searchMovieByGenre(genreId).moviesList
                _isVisible.value = false
            } catch (e: Exception){
                _error.value = noConnection
            }
        }
    }

    fun scheduleNotification(
        movie: Movie,
        scheduledTime: Calendar,
        context: Context,
        id: Int,
        formattedDate: String,
    ) {
        val data = Data.Builder()
        data.putString(NotifyWorker.NAME, context.getString(R.string.watch_later_invite))
        data.putString(NotifyWorker.BODY,
            context.getString(R.string.watch_the_movie) + " \"${movie.title}\"")
        data.putInt(NotifyWorker.ICON, R.drawable.ic_baseline_movie_24)
        data.putInt(NotifyWorker.ID, id)
        data.putInt(NotifyWorker.GRAPH, R.navigation.nav_graph)
        data.putInt(NotifyWorker.DESTINATION, R.id.detailsFragment)

        WorkManager
            .getInstance(context)
            .enqueue(buildWorkRequest(data, scheduledTime, movie.id.toString()))

        movie.isToNotify = true
        movie.reminder = formattedDate

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbRepository.insert(movie)
            }
        }
    }

    fun unscheduleNotification(context: Context, movie: Movie) {
        WorkManager
            .getInstance(context)
            .cancelAllWorkByTag(movie.id.toString())

        movie.isToNotify = false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbRepository.delete(movie)
            }
        }
    }

    private fun buildWorkRequest(
        data: Data.Builder,
        scheduledTime: Calendar,
        tag: String,
    ): WorkRequest {
        val currentTime = Calendar.getInstance()
        return OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .addTag(tag)
            .setInputData(data.build())
            .setInitialDelay(scheduledTime.timeInMillis - currentTime.timeInMillis,
                TimeUnit.MILLISECONDS)
            .build()
    }

//    private fun saveAll(movies: List<Movie>) {
//        Completable.fromRunnable { dbRepository.saveAll(movies) }
//            .subscribeOn(io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe()
//    }
//
//    fun selectAll(): Single<List<Movie>> {
//        return dbRepository.selectAll()
//    }
}