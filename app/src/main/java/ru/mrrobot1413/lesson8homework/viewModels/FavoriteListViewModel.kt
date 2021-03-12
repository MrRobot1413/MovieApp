package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.MovieDetailed
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository

class FavoriteListViewModel : ViewModel() {

    private var favoriteListRepository: FavoriteListRepository = FavoriteListRepository.getInstance()
    var movies = MutableLiveData<List<MovieDetailed>>()
    var error = MutableLiveData<String>()
    var movieDetailed = MutableLiveData<MovieDetailed>()

    fun getMovies(): LiveData<List<MovieDetailed>>{
        return favoriteListRepository.selectAll()
    }

    fun selectById(id: Int): MovieDetailed? {
        return favoriteListRepository.movieDao.selectById(id)
    }

    fun likeMovie(movie: MovieDetailed){
        favoriteListRepository.movieDao.insertMovie(movie)
    }

    fun delete(movie: MovieDetailed) {
        favoriteListRepository.movieDao.deleteMovie(movie)
    }

    fun getMoviesCount(): Int {
        return favoriteListRepository.movieDao.getMoviesCount()
    }
}