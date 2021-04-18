package ru.mrrobot1413.movieapp.model

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("results")
    val list: List<Video>? = null,
)