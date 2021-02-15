package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private lateinit var moviesList: MutableLiveData<List<Movie>>
    private lateinit var movieRepository: MovieRepository

    fun init() {
        movieRepository = MovieRepository().getInstansce()
        moviesList = movieRepository.getMovies()
    }

    fun getMovies(): LiveData<List<Movie>> {
        return moviesList
    }
}