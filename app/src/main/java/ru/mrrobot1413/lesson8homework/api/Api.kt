package ru.mrrobot1413.lesson8homework.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.mrrobot1413.lesson8homework.model.MovieResponse

interface Api {

    @GET("movie/popular&language=pt-BR")
    fun getMovies(
        @Query("api_key") apiKey: String = "82f337a96c72f107c937a1fcf9d4072c",
        @Query("page") page: Int
    ): Call<MovieResponse>
}