package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder

class MoviesAdapter(
    private var movies: MutableList<Movie>,
    private val clickListener: (movie: Movie) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    fun setMovies(movies: List<Movie>) {
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    fun setMoviesFromMenu(movies: List<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    override fun getItemCount() = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MoviesViewHolder(
            layoutInflater.inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(movies[position])
        setOnMovieClickListener(holder, movies[position])
    }

    private fun setOnMovieClickListener(holder: MoviesViewHolder, movie: Movie) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()
            clickListener(movie)
        }
    }
}