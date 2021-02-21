package ru.mrrobot1413.lesson8homework.model

import com.google.gson.annotations.SerializedName

data class SeriesResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val seriesList: List<Series>,
    @SerializedName("total_pages")
    val totalPages: Int
)
