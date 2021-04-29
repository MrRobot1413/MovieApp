package ru.mrrobot1413.movieapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var dbListRepository: DbListRepository = DbListRepository.getInstance()

    private val _favoriteMovies = MutableLiveData<List<Movie>>()
    val favoriteMovies: LiveData<List<Movie>> = _favoriteMovies

    private val _watchLaterMovies = MutableLiveData<List<Movie>>()
    val watchLaterMovies: LiveData<List<Movie>> = _watchLaterMovies

    private val _movieDetailed = MutableLiveData<Movie>()
    val movieDetailed: MutableLiveData<Movie> = _movieDetailed

    fun getFavoriteMovies() {
        viewModelScope.launch {
            _favoriteMovies.value = dbListRepository.selectAllFavorite()
        }
    }

    fun getWatchLaterList() {
        viewModelScope.launch {
            _watchLaterMovies.value = dbListRepository.selectWatchLaterList()
        }
    }

    fun selectById(id: Int) {
        viewModelScope.launch {
            _movieDetailed.value = dbListRepository.selectById(id)
        }
    }

    fun insert(movie: Movie) {
        viewModelScope.launch {
            dbListRepository.insert(movie)
        }
    }

    fun delete(movie: Movie) {
        viewModelScope.launch {
            dbListRepository.delete(movie)
        }
    }
}