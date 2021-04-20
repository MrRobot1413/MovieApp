package ru.mrrobot1413.movieapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "movies")
@Parcelize
data class Movie(
    @PrimaryKey @SerializedName("id") val id: Int,
    @ColumnInfo(name = "title") @SerializedName("title") val title: String,
    @ColumnInfo(name = "overview") @SerializedName("overview") val overview: String,
    @ColumnInfo(name = "posterPath") @SerializedName("poster_path") val posterPath: String?,
    @ColumnInfo(name = "rating") @SerializedName("vote_average") val rating: Float,
    @ColumnInfo(name = "release_date", defaultValue = "") @SerializedName("release_date") val releaseDate: String,
    @ColumnInfo(name = "time") @SerializedName("runtime") var time: Int,
    @ColumnInfo(name = "original_language") @SerializedName("original_language") val language: String,
    @ColumnInfo(name = "isLiked") var liked: Boolean = false,
    @ColumnInfo(name = "isToNotify") var isToNotify: Boolean = false,
    @ColumnInfo(name = "reminder") var reminder: String = ""
) :
    Parcelable