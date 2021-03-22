package ru.mrrobot1413.lesson8homework.interfaces

import androidx.navigation.fragment.FragmentNavigator
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series

interface MovieClickListener {
    fun openDetailsFragment(movie: Movie, extras: FragmentNavigator.Extras?)

    fun showBottomNav()

    fun hideBottomNav()
}