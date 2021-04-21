package ru.mrrobot1413.movieapp.interfaces

interface MovieClickListener {
    fun openDetailsFragment(id: Int, source: Int)

    fun showBottomNav()

    fun hideBottomNav()
}