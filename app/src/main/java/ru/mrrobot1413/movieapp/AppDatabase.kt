package ru.mrrobot1413.movieapp

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mrrobot1413.movieapp.dao.MovieDao
import ru.mrrobot1413.movieapp.model.Movie

@Database(entities = [Movie::class], version = 30)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}