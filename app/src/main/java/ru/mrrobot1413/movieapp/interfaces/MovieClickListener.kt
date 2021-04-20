package ru.mrrobot1413.movieapp.interfaces

import android.widget.RelativeLayout
import ru.mrrobot1413.movieapp.model.Movie

interface MovieClickListener {
    fun openDetailsFragment(id: Int, source: Int)

    fun showBottomNav()

    fun hideBottomNav()
}