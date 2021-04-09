package ru.mrrobot1413.movieapp.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    private val _movies = MutableLiveData<List<Movie>>()
    val movies : LiveData<List<Movie>> = _movies

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> = _error

    private val _movieDetailed = MutableLiveData<Movie>()
    val movieDetailed : LiveData<Movie> = _movieDetailed

    private val compositeDisposable = CompositeDisposable()

    companion object{
        const val WORK_TAG = "notificationTag"
    }

    fun getPopularMovies(
        page: Int,
    ) {
        val observable = movieRepository.getPopularMovies(page = page)

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result != null){
                    val list = ArrayList<Movie>()
                    _movies.postValue(result.moviesList)
                    if (_movies.value?.iterator()?.hasNext() == true) {
                            val next = _movies.value?.iterator()!!.next()
                            if (!next.liked) {
                                list.add(next)
                            }
                        }
                    saveAll(list)
                } else{
                    _error.postValue("No connection")
                }
            },
            {
                _error.postValue("Error loading movies")
            })

        compositeDisposable.add(observable)
    }

    fun getTopRatedMovies(
        page: Int,
    ) {
        val observable = movieRepository.getTopRatedMovies(page = page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result != null){
                    _movies.postValue(result.moviesList)
                } else{
                    _error.postValue("No connection")
                }
            },
                {
                    _error.postValue("Error loading movies")
                })

        compositeDisposable.add(observable)
    }

    fun getMovieDetails(
        id: Int,
    ) {
        val observable = movieRepository.getMovieDetails(id = id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result != null){
                    _movieDetailed.postValue(result)
                } else{
                    _error.postValue("No connection")
                }
            },
                {
                    _error.postValue("Error loading movies")
                })

        compositeDisposable.add(observable)
    }

    fun scheduleNotification(
        movieName: String,
        scheduledTime: Calendar,
        context: Context,
        id: Int
    ) {
        val data = Data.Builder()
        data.putString(NotifyWorker.NAME, context.getString(R.string.watch_later_invite))
        data.putString(NotifyWorker.BODY, context.getString(R.string.watch_the_movie) + " \"$movieName\"")
        data.putInt(NotifyWorker.ICON, R.drawable.ic_baseline_movie_24)
        data.putInt(NotifyWorker.ID, id)
        data.putInt(NotifyWorker.GRAPH, R.navigation.nav_graph)
        data.putInt(NotifyWorker.DESTINATION, R.id.detailsFragment)

        val currentTime = Calendar.getInstance()

        val workRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .addTag(WORK_TAG)
            .setInputData(data.build())
            .setInitialDelay(scheduledTime.timeInMillis - currentTime.timeInMillis,
                TimeUnit.MILLISECONDS)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }

    private fun saveAll(
        movies: List<Movie>,
    ) {
        dbRepository.saveAll(movies)
    }

    fun selectAll(): Single<List<Movie>> {
        return dbRepository.selectAll()
    }

    fun searchMovie(
        page: Int,
        query: String,
    ) {
        movieRepository.searchMovie(page = page, query = query)
        val observable = movieRepository.searchMovie(page = page, query = query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ result ->
                if(result != null){
                    _movies.postValue(result.moviesList)
                } else{
                    _error.postValue("No connection")
                }
            },
                {
                    _error.postValue("Error loading movies")
                })

        compositeDisposable.add(observable)
    }
}