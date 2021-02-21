package ru.mrrobot1413.lesson8homework.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series
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

    fun getSeries(
        page: Int = 1,
        onSuccess: ((movies: List<Series>) -> Unit),
        onError: (() -> Unit)
    ) {
        movieRepository.getSeries(page, onSuccess, onError)
    }
}