package ru.mrrobot1413.lesson8homework.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.mrrobot1413.lesson8homework.model.MovieDetailed

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun selectAll(): LiveData<List<MovieDetailed>>

    @Query("SELECT * FROM movies WHERE id=:id")
    fun selectById(id: Int): MovieDetailed?

    @Delete
    fun deleteMovie(movie: MovieDetailed)

    @Insert
    fun insertMovie(movie: MovieDetailed)

    @Query("SELECT count(*) FROM movies")
    fun getMoviesCount(): Int
}