package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()

//    fun getMovies(): LiveData<List<Movie>> {
//        return moviesList
//    }
}