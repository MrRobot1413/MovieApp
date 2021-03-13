package ru.mrrobot1413.lesson8homework.repositories

import androidx.lifecycle.LiveData
import ru.mrrobot1413.lesson8homework.App
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.Movie

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

    fun selectAll(): LiveData<List<Movie>> {
        return movieDao.selectAll()
    }

    fun selectById(id: Int): Movie? {
        return movieDao.selectById(id)
    }

    fun likeMovie(movie: Movie){
        movieDao.insertMovie(movie)
    }

    fun delete(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    fun getMoviesCount(): Int {
        return movieDao.getMoviesCount()
    }
}