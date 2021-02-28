package ru.mrrobot1413.lesson8homework.repositories

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mrrobot1413.lesson8homework.App
import ru.mrrobot1413.lesson8homework.model.MovieDetailResponse
import ru.mrrobot1413.lesson8homework.model.MovieResponse
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.model.SeriesResponse
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
    ): Call<MovieResponse>{
        return app.api.getPopularMovies(page = page, language = Locale.getDefault().language)
    }

    fun getMovieDetails(
        id: Int
    ): Call<MovieDetailResponse>{
        return app.api.getMovieDetails(id = id, language = Locale.getDefault().language)
    }

    fun getTopRatedMovies(
        page: Int = 1
    ): Call<MovieResponse>{
        return app.api.getTopRatedMovies(page = page, language = Locale.getDefault().language)
    }

    fun getSeries(
        page: Int = 1,
        onSuccess: (movies: List<Series>) -> Unit,
        onError: (() -> Unit)
    ) {
        when (Locale.getDefault().language) {
            "en" -> app.api.getSeries(page = page, language = "en")
                .enqueue(object : Callback<SeriesResponse> {
                    override fun onResponse(
                        call: Call<SeriesResponse>,
                        response: Response<SeriesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()

                            if (responseBody != null) {
                                onSuccess.invoke(responseBody.seriesList)
                            } else {
                                onError.invoke()
                            }
                        } else {
                            onError.invoke()
                        }
                    }

                    override fun onFailure(call: Call<SeriesResponse>, t: Throwable) {
                        onError.invoke()
                    }
                })
            "ru" -> app.api.getSeries(page = page, language = "ru")
                .enqueue(object : Callback<SeriesResponse> {
                    override fun onResponse(
                        call: Call<SeriesResponse>,
                        response: Response<SeriesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()

                            if (responseBody != null) {
                                onSuccess.invoke(responseBody.seriesList)
                            } else {
                                onError.invoke()
                            }
                        } else {
                            onError.invoke()
                        }
                    }

                    override fun onFailure(call: Call<SeriesResponse>, t: Throwable) {
                        onError.invoke()
                    }
                })
            "ukr" -> app.api.getSeries(page = page, language = "ukr")
                .enqueue(object : Callback<SeriesResponse> {
                    override fun onResponse(
                        call: Call<SeriesResponse>,
                        response: Response<SeriesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()

                            if (responseBody != null) {
                                onSuccess.invoke(responseBody.seriesList)
                            } else {
                                onError.invoke()
                            }
                        } else {
                            onError.invoke()
                        }
                    }

                    override fun onFailure(call: Call<SeriesResponse>, t: Throwable) {
                        onError.invoke()
                    }
                })
        }
    }
}
