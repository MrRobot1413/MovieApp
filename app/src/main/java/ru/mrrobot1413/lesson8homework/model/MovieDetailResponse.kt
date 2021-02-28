package ru.mrrobot1413.lesson8homework.model

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("revenue") val revenue: Int,
    @SerializedName("runtime") val time: Int,
    @SerializedName("original_language") val language: String
)