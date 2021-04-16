package ru.mrrobot1413.movieapp.viewModels

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mrrobot1413.movieapp.NotifyWorker
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    private var dbRepository: DbListRepository = DbListRepository.getInstance()

    private val _movies = MutableLiveData<List<MovieNetwork>>()
    val movies: LiveData<List<MovieNetwork>> = _movies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _movieDetailed = MutableLiveData<MovieNetwork>()
    val movieDetailed: LiveData<MovieNetwork> = _movieDetailed

    private val compositeDisposable = CompositeDisposable()

    companion object {
        const val WORK_TAG = "notificationTag"
    }

    fun getPopularMovies(
        page: Int,
        noConnection: String,
        errorLoading: String,
    ) {
        val observable = movieRepository.getPopularMovies(page = page)
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                if (result != null) {
//                    val list = ArrayList<Movie>()
                    _movies.postValue(result.moviesList)
//                    if (_movies.value?.iterator()?.hasNext() == true) {
//                        val next = _movies.value?.iterator()!!.next()
//                        list.add(Movie(
//                            next.id,
//                            next.title,
//                            next.overview,
//                            next.overview,
//                            next.rating,
//                            next.releaseDate,
//                            next.time,
//                            next.language
//                        ))
//                    }
//                    saveAll(list)
                } else {
                    _error.postValue(noConnection)
                }
            },
                {
                    _error.postValue(errorLoading)
                })

        compositeDisposable.add(observable)
    }

    fun getTopRatedMovies(
        page: Int,
        noConnection: String,
        errorLoading: String,
    ) {
        val observable = movieRepository.getTopRatedMovies(page = page)
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                if (result != null) {
                    _movies.postValue(result.moviesList)
                } else {
                    _error.postValue(noConnection)
                }
            },
                {
                    _error.postValue(errorLoading)
                })

        compositeDisposable.add(observable)
    }

    fun getMovieDetails(
        id: Int,
        noConnection: String,
        errorLoading: String,
    ) {
        val observable = movieRepository.getMovieDetails(id = id)
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                val repository = DbListRepository.getInstance()
                val movie = repository.selectById(id)
                movie
                    .subscribeOn(io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ movieFromDb ->
                        if (movieFromDb != null) {
                            _movieDetailed.postValue(
                                MovieNetwork(
                                    movieFromDb.id,
                                    movieFromDb.title,
                                    movieFromDb.overview,
                                    movieFromDb.posterPath,
                                    movieFromDb.rating,
                                    movieFromDb.releaseDate,
                                    movieFromDb.time,
                                    movieFromDb.language
                                )
                            )
                        } else {
                            _error.postValue(noConnection)
                        }
                    }, { _error.postValue(noConnection) })
            }
            .subscribe({ result ->
                if (result != null) {
                    _movieDetailed.postValue(result)
                } else {
                    _error.postValue(noConnection)
                }
            },
                {
                    _error.postValue(errorLoading)
                })

        compositeDisposable.add(observable)
    }

    fun searchMovie(
        page: Int,
        query: String,
        noConnection: String,
        errorLoading: String,
    ) {
        if (query != null) {
            if (query.length >= 2) {
                val observable = movieRepository.searchMovie(page = page, query = query)
                    .subscribeOn(io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delay(500, TimeUnit.MILLISECONDS)
                    .subscribe({ result ->
                        if (result != null) {
                            _movies.postValue(result.moviesList)
                        } else {
                            _error.postValue(errorLoading)
                        }
                    },
                        {
                            _error.postValue(noConnection)
                        })

                compositeDisposable.add(observable)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun scheduleNotification(
        movieName: String,
        scheduledTime: Calendar,
        context: Context,
        id: Int,
    ) {
        val data = Data.Builder()
        data.putString(NotifyWorker.NAME, context.getString(R.string.watch_later_invite))
        data.putString(NotifyWorker.BODY,
            context.getString(R.string.watch_the_movie) + " \"$movieName\"")
        data.putInt(NotifyWorker.ICON, R.drawable.ic_baseline_movie_24)
        data.putInt(NotifyWorker.ID, id)
        data.putInt(NotifyWorker.GRAPH, R.navigation.nav_graph)
        data.putInt(NotifyWorker.DESTINATION, R.id.detailsFragment)

        WorkManager
            .getInstance(context)
            .enqueue(buildWorkRequest(data, scheduledTime))
    }

    private fun buildWorkRequest(
        data: Data.Builder,
        scheduledTime: Calendar,
    ): WorkRequest {
        val currentTime = Calendar.getInstance()
        return OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .addTag(WORK_TAG)
            .setInputData(data.build())
            .setInitialDelay(scheduledTime.timeInMillis - currentTime.timeInMillis,
                TimeUnit.MILLISECONDS)
            .build()
    }

    private fun saveAll(movies: List<Movie>) {
        Completable.fromRunnable { dbRepository.saveAll(movies) }
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun selectAll(): Single<List<Movie>> {
        return dbRepository.selectAll()
    }
}