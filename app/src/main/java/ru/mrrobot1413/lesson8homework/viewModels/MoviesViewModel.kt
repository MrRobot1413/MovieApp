package ru.mrrobot1413.lesson8homework.viewModels

import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.repositories.MovieRepository

class MoviesViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()

    fun getPopularMovies(
        page: Int = 1,
        onSuccess: ((movies: List<Movie>) -> Unit),
        onError: (() -> Unit)
    ) {
        movieRepository.getPopularMovies(page, onSuccess, onError)
    }

    fun getTopRatedMovies(
        page: Int = 1,
        onSuccess: ((movies: List<Movie>) -> Unit),
        onError: (() -> Unit)
    ) {
        movieRepository.getTopRatedMovies(page, onSuccess, onError)
    }

    fun getSeries(
        page: Int = 1,
        onSuccess: ((movies: List<Series>) -> Unit),
        onError: (() -> Unit)
    ) {
        movieRepository.getSeries(page, onSuccess, onError)
    }
}