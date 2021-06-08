package ru.mrrobot1413.movieapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "movies")
@Parcelize
data class Movie(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "posterPath") val posterPath: String?,
    @ColumnInfo(name = "rating") val rating: Float,
    @ColumnInfo(name = "release_date", defaultValue = "") val releaseDate: String,
    @ColumnInfo(name = "time") var time: Int,
    @ColumnInfo(name = "original_language") val language: String,
    @ColumnInfo(name = "isLiked") var liked: Boolean = false,
    @ColumnInfo(name = "isToNotify") var isToNotify: Boolean = false,
    @ColumnInfo(name = "reminder") var reminder: String = ""
) :
    Parcelable