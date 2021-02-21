package ru.mrrobot1413.lesson8homework.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

//@Entity(tableName = "series")
//@ColumnInfo(name = "title")
//@ColumnInfo(name = "overview")
//@ColumnInfo(name = "posterPath")
//@ColumnInfo(name = "rating")
//@ColumnInfo(name = "release_date")
//@ColumnInfo(name = "original_language")
@Parcelize
data class Series(
    @PrimaryKey @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("first_air_date") val releaseDate: String,
    @SerializedName("original_language") val language: String
) :
    Parcelable {
    @ColumnInfo(name = "isLiked")
    @IgnoredOnParcel
    var liked = false
}
