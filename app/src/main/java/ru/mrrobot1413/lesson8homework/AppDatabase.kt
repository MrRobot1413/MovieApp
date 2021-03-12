package ru.mrrobot1413.lesson8homework

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mrrobot1413.lesson8homework.dao.MovieDao
import ru.mrrobot1413.lesson8homework.model.MovieDetailed

@Database(entities = [MovieDetailed::class], version = 23)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}