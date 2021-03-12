package ru.mrrobot1413.lesson8homework.repositories

import androidx.lifecycle.LiveData
import ru.mrrobot1413.lesson8homework.App
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.MovieDetailed

object FavoriteListRepository {

    private lateinit var instance: FavoriteListRepository

    private val db by lazy {
        App().getInstance().getDatabase()
    }

    lateinit var movieDao: MovieDao

    fun getInstance(): FavoriteListRepository {
        instance = this

        movieDao = db.movieDao()

        return instance
    }

    fun selectAll(): LiveData<List<MovieDetailed>> {
        return movieDao.selectAll()
    }

    fun selectById(id: Int): MovieDetailed? {
        return movieDao.selectById(id)
    }

    fun likeMovie(movie: MovieDetailed){
        movieDao.insertMovie(movie)
    }

    fun delete(movie: MovieDetailed) {
        movieDao.deleteMovie(movie)
    }

    fun getMoviesCount(): Int {
        return movieDao.getMoviesCount()
    }
}