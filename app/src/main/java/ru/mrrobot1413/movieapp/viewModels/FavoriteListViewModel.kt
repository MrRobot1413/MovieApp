package ru.mrrobot1413.movieapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var dbListRepository: DbListRepository = DbListRepository.getInstance()

    private val _favoriteMovies = MutableLiveData<List<Movie>>()
    val favoriteMovies: LiveData<List<Movie>> = _favoriteMovies

    private val _watchLaterMovies = MutableLiveData<List<Movie>>()
    val watchLaterMovies: LiveData<List<Movie>> = _watchLaterMovies

    private val _movieDetailed = MutableLiveData<Movie>()
    val movieDetailed: MutableLiveData<Movie> = _movieDetailed

    fun getFavoriteMovies(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _favoriteMovies.postValue(dbListRepository.selectAllFavorite())
            }
        }
    }

    fun getWatchLaterList(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _watchLaterMovies.postValue(dbListRepository.selectWatchLaterList())
            }
        }
    }

    fun selectById(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _movieDetailed.postValue(dbListRepository.selectById(id))
            }
        }
    }

    fun insert(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                dbListRepository.insert(movie)
            }
        }
    }

    fun delete(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                dbListRepository.delete(movie)
            }
        }
    }
}