package ru.mrrobot1413.lesson8homework.viewModels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.MovieResponse
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    var movies = MutableLiveData<List<Movie>>()
    var error = MutableLiveData<String>()

    fun getPopularMovies(
        page: Int
    ) {
        movieRepository.getPopularMovies(page).enqueue(object : Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            movies.postValue(responseBody.moviesList)
                        } else {
                            error.postValue("Error loading movies")
                        }
                    } else {
                        error.postValue("Error loading movies")
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
//                    onError.invoke()
                    error.postValue("Error loading movies")
                }
        })
    }

    fun getTopRatedMovies(
        page: Int
    ) {
        movieRepository.getTopRatedMovies(page).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        movies.postValue(responseBody.moviesList)
                    } else {
                        error.postValue("Error loading movies")
                    }
                } else {
//                        onError.invoke()
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
//                    onError.invoke()
                error.postValue("Error loading movies")
            }
        })
    }

    fun getSeries(
        page: Int = 1,
        onSuccess: ((movies: List<Series>) -> Unit),
        onError: (() -> Unit)
    ) {
        movieRepository.getSeries(page, onSuccess, onError)
    }
}