package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var favoriteListRepository: DbListRepository = DbListRepository.getInstance()
    var movies = MutableLiveData<List<Movie>>()

    fun getFavoriteMovies(): LiveData<List<Movie>>{
        return favoriteListRepository.selectAllFavorite()
    }

    fun selectById(id: Int): Movie? {
        return favoriteListRepository.movieDao.selectById(id)
    }

    fun likeMovie(movie: Movie){
        favoriteListRepository.likeMovie(movie)
    }

    fun delete(movie: Movie) {
        favoriteListRepository.delete(movie)
    }

    fun getMoviesCount(): Int {
        return favoriteListRepository.getMoviesCount()
    }
}