package ru.mrrobot1413.movieapp.repositories

import androidx.lifecycle.LiveData
import ru.mrrobot1413.movieapp.App
import ru.mrrobot1413.movieapp.dao.MovieDao
import ru.mrrobot1413.movieapp.model.Movie

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

    suspend fun selectAllFavorite(): List<Movie> {
        return movieDao.selectAllFavorite()
    }

    suspend fun selectWatchLaterList(): List<Movie> {
        return movieDao.selectWatchLaterList()
    }

//    suspend fun selectAll(): LiveData<List<Movie>> {
//        return movieDao.selectAll()
//    }
//
//    suspend fun saveAll(movies: List<Movie>){
//        movieDao.saveAll(movies)
//    }

    suspend fun selectById(id: Int): Movie {
        return movieDao.selectById(id)
    }

    suspend fun insert(movie: Movie){
        movieDao.insertMovie(movie)
    }

    suspend fun delete(movie: Movie) {
        if(movie.isToNotify && !movie.liked){
            movieDao.insertMovie(movie)
        } else if(!movie.isToNotify && !movie.liked){
            movieDao.deleteMovie(movie)
        } else if(movie.liked && !movie.isToNotify){
            movieDao.insertMovie(movie)
        }
    }
}