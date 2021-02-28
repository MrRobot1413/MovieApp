package ru.mrrobot1413.lesson8homework.repositories

import android.util.Log
import ru.mrrobot1413.lesson8homework.App
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.MovieDetailResponse

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

    fun selectAll(): List<Movie> {
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