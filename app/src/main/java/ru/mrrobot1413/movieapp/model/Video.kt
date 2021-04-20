package ru.mrrobot1413.movieapp.model

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("key") val key: String = ""
)