package ru.mrrobot1413.lesson8homework.repositories

import androidx.lifecycle.LiveData
import ru.mrrobot1413.lesson8homework.App
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.Movie

object DbListRepository {

    private lateinit var instance: DbListRepository

    private val db by lazy {
        App().getInstance().getDatabase()
    }

    lateinit var movieDao: MovieDao

    fun getInstance(): DbListRepository {
        instance = this

        movieDao = db.movieDao()

        return instance
    }

    fun selectAllFavorite(): LiveData<List<Movie>> {
        return movieDao.selectAllFavorite()
    }

    fun selectAll(): LiveData<List<Movie>> {
        return movieDao.selectAll()
    }

    fun saveAll(movies: List<Movie>){
        movieDao.saveAll(movies)
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