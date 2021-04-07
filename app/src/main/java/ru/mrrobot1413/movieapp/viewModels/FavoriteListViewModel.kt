package ru.mrrobot1413.movieapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var dbListRepository: DbListRepository = DbListRepository.getInstance()

    fun getFavoriteMovies(): LiveData<List<Movie>>{
        return dbListRepository.selectAllFavorite()
    }

//    fun getWatchLaterList(): LiveData<List<Movie>>{
//        return dbListRepository.selectWatchLaterList()
//    }

    fun selectById(id: Int): Movie? {
        return dbListRepository.movieDao.selectById(id)
    }

    fun insert(movie: Movie) {
        dbListRepository.insert(movie)
    }

    fun delete(movie: Movie) {
        dbListRepository.delete(movie)
    }
}