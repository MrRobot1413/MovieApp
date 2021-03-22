package ru.mrrobot1413.lesson8homework.interfaces

import android.widget.ImageView
import androidx.navigation.fragment.FragmentNavigator
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series

interface MovieClickListener {
    fun openDetailsFragment(movie: Movie, imageView: ImageView)

    fun showBottomNav()

    fun hideBottomNav()
}