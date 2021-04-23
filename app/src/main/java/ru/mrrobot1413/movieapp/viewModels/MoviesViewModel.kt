package ru.mrrobot1413.movieapp.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.*
import ru.mrrobot1413.movieapp.NotifyWorker
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import java.util.*
import java.util.concurrent.TimeUnit


class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    private var dbRepository: DbListRepository = DbListRepository.getInstance()

    private val _movies = MutableLiveData<List<MovieNetwork>>()
    val movies: LiveData<List<MovieNetwork>> = _movies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _movieDetailed = MutableLiveData<MovieNetwork>()
    val movieDetailed: LiveData<MovieNetwork> = _movieDetailed

    private val _videoKey = MutableLiveData<String>()
    val videoKey = _videoKey

    fun getPopularMovies(
        page: Int,
        noConnection: String,
        errorLoading: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = movieRepository.getPopularMovies(page = page).await()
                if (request.isSuccessful) {
                    if (request.body() != null) {
                        _movies.postValue(request.body()!!.moviesList)
                    } else {
                        _error.postValue(errorLoading)
                    }
                } else {
                    _error.postValue(noConnection)
                }
            }
        }
    }

    fun getTopRatedMovies(
        page: Int,
        noConnection: String,
        errorLoading: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = movieRepository.getTopRatedMovies(page = page).await()
                if (request.isSuccessful) {
                    if (request.body() != null) {
                        _movies.postValue(request.body()!!.moviesList)
                    } else {
                        _error.postValue(errorLoading)
                    }
                } else {
                    _error.postValue(noConnection)
                }
            }
        }
    }

    fun getMovieDetails(
        id: Int,
        errorLoading: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = movieRepository.getMovieDetails(id = id).await()
                if (request.isSuccessful) {
                    if (request.body() != null) {
                        val movie = request.body()!!
                        _movieDetailed.postValue(movie)
                    } else {
                        _error.postValue(errorLoading)
                    }
                } else {
                    viewModelScope.launch {
//                        val repository = DbListRepository.getInstance()
//                        val movie = repository.selectById(id).await().body()
//                        if (movie != null) {
//                            _movieDetailed.postValue(movie)
//                        }
                    }
                }
            }
        }
    }

    fun searchMovie(
        page: Int,
        query: String?,
        noConnection: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (query?.length!! >= 2) {
                    val request = movieRepository.searchMovie(page = page, query = query).await()
                    delay(700)
                    if (request.isSuccessful) {
                        _movies.postValue(request.body()?.moviesList)
                    } else {
                        _error.postValue(noConnection)
                    }
                }
            }
        }
    }

    fun getVideos(
        id: Int,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = movieRepository.getVideos(id).await()
                if (request.isSuccessful) {
                        if(request.body()?.list?.isEmpty() == true){
                            _videoKey.postValue("https://www.youtube.com/results?search_query=${_movieDetailed.value?.title + " " + _movieDetailed.value?.releaseDate} trailer")
                        } else{
                            _videoKey.postValue("http://www.youtube.com/watch?v=${request.body()?.list?.get(0)?.key}")
                        }
                    } else{
                    _videoKey.postValue("https://www.youtube.com/results?search_query=${_movieDetailed.value?.title} trailer")
                }
            }
        }
    }

    fun searchMovieByGenre(
        genreId: Int,
        noConnection: String,
        errorLoading: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = movieRepository.searchMovieByGenre(genreId).await()
                if (request.isSuccessful) {
                    if (request.body() != null) {
                        _movies.postValue(request.body()!!.moviesList)
                    } else {
                        _error.postValue(errorLoading)
                    }
                } else {
                    _error.postValue(noConnection)
                }
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
            withContext(Dispatchers.IO){
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
            withContext(Dispatchers.IO){
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