package ru.mrrobot1413.lesson8homework.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.mrrobot1413.lesson8homework.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun selectAll(): List<Movie>

    @Delete
    fun deleteMovie(movie: Movie)

    @Insert
    fun insertMovie(movie: Movie)

    @Query("SELECT count(*) FROM movies")
    fun getMoviesCount(): Int
}