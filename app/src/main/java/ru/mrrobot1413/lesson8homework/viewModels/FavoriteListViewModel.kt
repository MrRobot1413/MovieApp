package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.*
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository

class FavoriteListViewModel : ViewModel() {

    private var favoriteListRepository: FavoriteListRepository = FavoriteListRepository.getInstance()
    private var moviesList: List<Movie> = favoriteListRepository.selectAll()

    fun getMovies(): List<Movie> {
        return moviesList
    }
}