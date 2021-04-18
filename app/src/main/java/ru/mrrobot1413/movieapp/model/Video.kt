package ru.mrrobot1413.movieapp.model

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("key") val key: String = ""
)