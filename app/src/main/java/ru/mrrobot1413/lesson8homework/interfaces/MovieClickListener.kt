package ru.mrrobot1413.lesson8homework.interfaces

import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series

interface MovieClickListener {
    fun onClick(movie: Movie)

    fun restoreBottomNav()
}