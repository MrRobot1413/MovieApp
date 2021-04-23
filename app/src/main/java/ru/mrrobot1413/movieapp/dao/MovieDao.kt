package ru.mrrobot1413.movieapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.mrrobot1413.movieapp.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE isLiked is 1")
    fun selectAllFavorite(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE isToNotify is 1")
    fun selectWatchLaterList(): LiveData<List<Movie>>

//    suspend fun selectAll(): LiveData<List<Movie>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveAll(movie: List<Movie>)
//    @Query("SELECT * FROM movies")

    @Query("SELECT * FROM movies WHERE id=:id")
    fun selectById(id: Int): LiveData<Movie>

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)
}