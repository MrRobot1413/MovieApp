package ru.mrrobot1413.lesson8homework.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Movie(
    val movieName: Int,
    val movieTime: Int,
    val movieImage: Int,
    val movieRating: Int,
    val movieActors: Int,
    val movieDescr: Int,
    val movieInviteText: Int
) :
    Parcelable{
        @IgnoredOnParcel
        var liked = false
    }
