package ru.mrrobot1413.lesson8homework.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.mrrobot1413.lesson8homework.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE isLiked is 1")
    fun selectAllFavorite(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies")
    fun selectAll(): LiveData<List<Movie>>

    @Insert
    fun saveAll(movie: List<Movie>)

    @Query("SELECT * FROM movies WHERE id=:id")
    fun selectById(id: Int): Movie?

    @Delete
    fun deleteMovie(movie: Movie)

    @Insert
    fun insertMovie(movie: Movie)

    @Query("SELECT count(*) FROM movies")
    fun getMoviesCount(): Int
}