package ru.mrrobot1413.movieapp.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.model.MovieResponse
import ru.mrrobot1413.movieapp.model.VideoResponse

interface Api {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): MovieNetwork

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("page") page: Int,
        @Query("language") language: String,
        @Query("query") query: String
    ): MovieResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getVideos(
        @Path("movie_id") id: Int,
        @Query("language") language: String
    ): VideoResponse

    @GET("discover/movie")
    suspend fun searchByGenre(
        @Query("with_genres") id: Int?,
        @Query("language") language: String
    ): MovieResponse
}