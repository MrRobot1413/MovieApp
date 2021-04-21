package ru.mrrobot1413.movieapp.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.work.impl.Schedulers
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.repositories.DbListRepository

class FavoriteListViewModel : ViewModel() {

    private var dbListRepository: DbListRepository = DbListRepository.getInstance()

    fun getFavoriteMovies(): Single<List<Movie>>{
        return dbListRepository.selectAllFavorite()
    }

    fun getWatchLaterList(): Single<List<Movie>>{
        return dbListRepository.selectWatchLaterList()
    }

    fun selectById(id: Int): Single<Movie?> {
        return dbListRepository.selectById(id)
    }

    @SuppressLint("CheckResult")
    fun insert(movie: Movie) {
        Completable.fromAction {
            dbListRepository.insert(movie)
        }
            .subscribeOn(io())
            .subscribe()
    }

    @SuppressLint("CheckResult")
    fun delete(movie: Movie) {
        Completable.fromAction {
            dbListRepository.delete(movie)
        }
            .subscribeOn(io())
            .subscribe()
    }
}