package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.interfaces.OnGetMoviesCallback
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()
    fun getMovies(
        page: Int = 1,
        onSuccess: ((movies: List<Movie>) -> Unit),
        onError: (() -> Unit)
    ) {
        movieRepository.getMovies(page, onSuccess, onError)
    }

}