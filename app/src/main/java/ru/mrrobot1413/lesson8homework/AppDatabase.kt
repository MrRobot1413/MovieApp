package ru.mrrobot1413.lesson8homework

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.Movie

@Database(entities = [Movie::class], version = 26)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}