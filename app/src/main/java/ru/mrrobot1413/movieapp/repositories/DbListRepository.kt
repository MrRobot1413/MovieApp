package ru.mrrobot1413.movieapp.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        return withContext(Dispatchers.IO){movieDao.selectAllFavorite()}
    }

    suspend fun selectWatchLaterList(): List<Movie> {
        return withContext(Dispatchers.IO){movieDao.selectWatchLaterList()}
    }

//    suspend fun selectAll(): LiveData<List<Movie>> {
//        return movieDao.selectAll()
//    }
//
//    suspend fun saveAll(movies: List<Movie>){
//        movieDao.saveAll(movies)
//    }

    suspend fun selectById(id: Int): Movie {
        return withContext(Dispatchers.IO){movieDao.selectById(id)}
    }

    suspend fun insert(movie: Movie){
        withContext(Dispatchers.IO){movieDao.insertMovie(movie)}
    }

    suspend fun delete(movie: Movie) {
        withContext(Dispatchers.IO){if(movie.isToNotify && !movie.liked){
            movieDao.insertMovie(movie)
        } else if(!movie.isToNotify && !movie.liked){
            movieDao.deleteMovie(movie)
        } else if(movie.liked && !movie.isToNotify){
            movieDao.insertMovie(movie)
        }}
    }
}