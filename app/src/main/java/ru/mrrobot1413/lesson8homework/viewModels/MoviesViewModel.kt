package ru.mrrobot1413.lesson8homework.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mrrobot1413.lesson8homework.model.MovieDetailed
import ru.mrrobot1413.lesson8homework.model.MovieResponse
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    var movies = MutableLiveData<List<MovieDetailed>>()
    var error = MutableLiveData<String>()
    var movieDetailed = MutableLiveData<MovieDetailed>()

    fun getPopularMovies(
        page: Int
    ) {
        movieRepository.getPopularMovies(page = page).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>,
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
                error.postValue("No connection")
            }
        })
    }

    fun getTopRatedMovies(
        page: Int,
    ) {
        movieRepository.getTopRatedMovies(page).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>,
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
                error.postValue("No connection")
            }
        })
    }

    fun getMovieDetails(
        id: Int,
    ) {
        movieRepository.getMovieDetails(id = id).enqueue(object : Callback<MovieDetailed> {
            val repository = FavoriteListRepository.getInstance()
            val movie = repository.selectById(id)

            override fun onResponse(
                call: Call<MovieDetailed>,
                response: Response<MovieDetailed>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("repos", movie.toString())
                        movieDetailed.postValue(
                            MovieDetailed(
                                responseBody.id,
                                responseBody.title,
                                responseBody.overview,
                                responseBody.posterPath,
                                responseBody.rating,
                                responseBody.releaseDate,
                                responseBody.time,
                                responseBody.language
                            )
                        )
                    } else {
                        error.postValue("Error loading movies")
                        Log.d("movieees1", movie.toString())
                        movieDetailed.postValue(
                            movie
                        )
                    }
                } else {
                    error.postValue("Error loading movies")
                    Log.d("movieees2", movie.toString())
                    movieDetailed.postValue(
                        movie
                    )
                }
            }

            override fun onFailure(call: Call<MovieDetailed>, t: Throwable) {
                if (movie != null) {
                    Log.d("movieees3", movie.toString())
                    movieDetailed.postValue(
                        movie
                    )
                } else{
                    error.postValue("No connection")
                }
            }
        })
    }

    fun getSeries(
        page: Int = 1,
        onSuccess: ((movies: List<Series>) -> Unit),
        onError: (() -> Unit),
    ) {
        movieRepository.getSeries(page, onSuccess, onError)
    }
}