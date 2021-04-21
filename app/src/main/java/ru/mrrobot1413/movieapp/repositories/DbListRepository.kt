package ru.mrrobot1413.movieapp.repositories

import io.reactivex.Single
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

    fun selectAllFavorite(): Single<List<Movie>> {
        return movieDao.selectAllFavorite()
    }

    fun selectWatchLaterList(): Single<List<Movie>> {
        return movieDao.selectWatchLaterList()
    }

    fun selectAll(): Single<List<Movie>> {
        return movieDao.selectAll()
    }

    fun saveAll(movies: List<Movie>){
        movieDao.saveAll(movies)
    }

    fun selectById(id: Int): Single<Movie?> {
        return movieDao.selectById(id)
    }

    fun insert(movie: Movie){
        movieDao.insertMovie(movie)
    }

    fun delete(movie: Movie) {
        if(movie.isToNotify && !movie.liked){
            movieDao.insertMovie(movie)
        } else if(!movie.isToNotify && !movie.liked){
            movieDao.deleteMovie(movie)
        } else if(movie.liked && !movie.isToNotify){
            movie.isToNotify = false
            movie.liked = true
            movieDao.insertMovie(movie)
        }
    }
}