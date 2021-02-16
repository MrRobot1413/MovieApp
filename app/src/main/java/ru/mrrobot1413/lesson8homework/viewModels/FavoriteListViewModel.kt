package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository

class FavoriteListViewModel : ViewModel() {

    private var favoriteListRepository: FavoriteListRepository = FavoriteListRepository().getInstansce()
    private var moviesList: MutableLiveData<List<Movie>> = favoriteListRepository.getMovies()

    fun getMovies(): LiveData<List<Movie>> {
        return moviesList
    }
}