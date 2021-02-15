package ru.mrrobot1413.lesson8homework.repositories

import androidx.lifecycle.MutableLiveData
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.Movie

class MovieRepository {
    private lateinit var instance: MovieRepository
    private var dataSet: MutableList<Movie> = DataStorage.moviesList

    fun getInstansce(): MovieRepository {
        instance = MovieRepository()

        return instance
    }

    fun getMovies(): MutableLiveData<List<Movie>> {
        val data: MutableLiveData<List<Movie>> = MutableLiveData()
        data.value = dataSet
        return data
    }
}