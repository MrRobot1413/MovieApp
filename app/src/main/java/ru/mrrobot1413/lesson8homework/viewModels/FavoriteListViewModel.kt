package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository

class FavoriteListViewModel : ViewModel() {

    private var favoriteListRepository: FavoriteListRepository = FavoriteListRepository.getInstance()
    var movies = MutableLiveData<List<Movie>>()
    var error = MutableLiveData<String>()
    var movieDetailed = MutableLiveData<Movie>()

    fun getMovies(): LiveData<List<Movie>>{
        return favoriteListRepository.selectAll()
    }

    fun selectById(id: Int): Movie? {
        return favoriteListRepository.movieDao.selectById(id)
    }

    fun likeMovie(movie: Movie){
        favoriteListRepository.movieDao.insertMovie(movie)
    }

    fun delete(movie: Movie) {
        favoriteListRepository.movieDao.deleteMovie(movie)
    }

    fun getMoviesCount(): Int {
        return favoriteListRepository.movieDao.getMoviesCount()
    }
}