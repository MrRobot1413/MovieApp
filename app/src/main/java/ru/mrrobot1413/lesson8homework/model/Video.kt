package ru.mrrobot1413.lesson8homework.model

import com.google.gson.annotations.SerializedName

data class Video(
    val id: Int,
    @SerializedName("results")
    val results: List<ResultVideo>
)