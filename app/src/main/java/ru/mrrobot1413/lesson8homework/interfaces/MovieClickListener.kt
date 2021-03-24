package ru.mrrobot1413.lesson8homework.interfaces

import android.widget.RelativeLayout
import ru.mrrobot1413.lesson8homework.model.Movie

interface MovieClickListener {
    fun openDetailsFragment(movie: Movie, holder: RelativeLayout)

    fun showBottomNav()

    fun hideBottomNav()
}