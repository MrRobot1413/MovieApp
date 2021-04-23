package ru.mrrobot1413.movieapp.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.model.VideoResponse

interface Api {

    companion object {
        const val API_KEY = "82f337a96c72f107c937a1fcf9d4072c"
    }

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): Deferred<Response<MovieResponse>>

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): Deferred<Response<MovieResponse>>

    @GET("movie/{id}")
    fun getMovieDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Deferred<Response<MovieNetwork>>

    @GET("search/movie")
    fun searchMovie(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String,
        @Query("query") query: String
    ): Deferred<Response<MovieResponse>>

    @GET("movie/{movie_id}/videos")
    fun getVideos(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String
    ): Deferred<Response<VideoResponse>>

    @GET("discover/movie")
    fun searchByGenre(
        @Query("with_genres") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String
    ): Deferred<Response<MovieResponse>>
}