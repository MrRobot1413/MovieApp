package ru.mrrobot1413.lesson3homework.interfaces

import ru.mrrobot1413.lesson3homework.model.Movie

interface FragmentsClickListener {
    fun onClick(movie: Movie)
}