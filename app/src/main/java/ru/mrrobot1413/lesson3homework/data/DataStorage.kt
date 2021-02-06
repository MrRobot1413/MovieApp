package ru.mrrobot1413.lesson3homework.data

import ru.mrrobot1413.lesson3homework.model.Movie
import ru.mrrobot1413.lesson3homework.R

object DataStorage {
    var moviesList: MutableList<Movie> = mutableListOf(
        Movie(
            R.string.movie_3_name,
            R.string.movie_3_time,
            R.drawable.movie_3_image,
            R.string.movie_3_rating,
            R.string.movie_3_actors,
            R.string.movie_3_descr,
            R.string.movie_3_invite
        ),
        Movie(
            R.string.movie_1_name,
            R.string.movie_1_time,
            R.drawable.movie_1_image,
            R.string.movie_1_rating,
            R.string.movie_1_actors,
            R.string.movie_1_descr,
            R.string.movie_1_invite
        ),
        Movie(
            R.string.movie_4_name,
            R.string.movie_4_time,
            R.drawable.movie_4_image,
            R.string.movie_4_rating,
            R.string.movie_4_actors,
            R.string.movie_4_descr,
            R.string.movie_4_invite
        ),
        Movie(
            R.string.movie_2_name,
            R.string.movie_2_time,
            R.drawable.movie_2_image,
            R.string.movie_2_rating,
            R.string.movie_2_actors,
            R.string.movie_2_descr,
            R.string.movie_2_invite
        ),
        Movie(
            R.string.movie_5_name,
            R.string.movie_5_time,
            R.drawable.movie_5_image,
            R.string.movie_5_rating,
            R.string.movie_5_actors,
            R.string.movie_5_descr,
            R.string.movie_5_invite
        )
    )

    var favoriteList: MutableList<Movie> = mutableListOf()
}