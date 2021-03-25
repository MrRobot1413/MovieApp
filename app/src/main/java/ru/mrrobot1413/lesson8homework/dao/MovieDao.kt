package ru.mrrobot1413.lesson8homework.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.mrrobot1413.lesson8homework.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE isLiked is 1")
    fun selectAllFavorite(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies")
    fun selectAll(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(movie: List<Movie>)

    @Query("SELECT * FROM movies WHERE id=:id")
    fun selectById(id: Int): Movie?

    @Delete
    fun deleteMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie)

    @Query("SELECT count(*) FROM movies")
    fun getMoviesCount(): Int
}