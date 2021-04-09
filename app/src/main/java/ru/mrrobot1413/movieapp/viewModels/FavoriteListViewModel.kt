package ru.mrrobot1413.movieapp.viewModels

import androidx.lifecycle.ViewModel
import io.reactivex.Single
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var dbListRepository: DbListRepository = DbListRepository.getInstance()

    fun getFavoriteMovies(): Single<List<Movie>>{
        return dbListRepository.selectAllFavorite()
    }

//    fun getWatchLaterList(): LiveData<List<Movie>>{
//        return dbListRepository.selectWatchLaterList()
//    }

    fun selectById(id: Int): Single<Movie?> {
        return dbListRepository.selectById(id)
    }

    fun insert(movie: Movie) {
        dbListRepository.insert(movie)
    }

    fun delete(movie: Movie) {
        dbListRepository.delete(movie)
    }
}