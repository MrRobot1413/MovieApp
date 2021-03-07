package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.MovieDetailed
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder

class MoviesAdapter(
    private var movies: MutableList<MovieDetailed>,
    private val clickListener: (movie: MovieDetailed) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    fun appendMovies(movies: List<MovieDetailed>) {
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    fun appendMoviesFromMenu(movies: List<MovieDetailed>) {
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
        setOnDetailsClickListener(holder, movies[position])
    }

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: MovieDetailed) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()

            clickListener(movie)
        }
    }
}