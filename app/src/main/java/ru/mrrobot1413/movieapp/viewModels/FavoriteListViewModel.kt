package ru.mrrobot1413.movieapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.repositories.DbListRepository
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(dbRepo: DbListRepository): ViewModel() {

    private var dbListRepository: DbListRepository = dbRepo

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(mutableListOf())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    private val _watchLaterMovies = MutableStateFlow<List<Movie>>(mutableListOf())
    val watchLaterMovies: StateFlow<List<Movie>> = _watchLaterMovies

    private val _movieDetailed = MutableStateFlow<Movie?>(null)
    val movieDetailed: StateFlow<Movie?> = _movieDetailed

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