package ru.mrrobot1413.movieapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var dbListRepository: DbListRepository = DbListRepository.getInstance()

    fun getFavoriteMovies(): LiveData<List<Movie>>{
        return dbListRepository.selectAllFavorite()
    }

    fun getWatchLaterList(): LiveData<List<Movie>>{
        return dbListRepository.selectWatchLaterList()
    }

    fun selectById(id: Int): LiveData<Movie> {
        return dbListRepository.selectById(id)
    }

    fun insert(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                dbListRepository.insert(movie)
            }
        }
    }

    fun delete(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                dbListRepository.delete(movie)
            }
        }
    }
}