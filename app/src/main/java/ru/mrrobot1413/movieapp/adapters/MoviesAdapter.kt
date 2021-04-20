package ru.mrrobot1413.movieapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.viewHolders.MoviesViewHolder

class MoviesAdapter(
    private var movies: MutableList<MovieNetwork>,
    private val clickListener: (id: Int) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    fun setMovies(movies: List<MovieNetwork>) {
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    fun setMoviesFromMenu(movies: List<MovieNetwork>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    override fun getItemCount() = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MoviesViewHolder(
            layoutInflater.inflate(R.layout.movie_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(movies[position])
        setOnMovieClickListener(holder, movies[position])
    }

    private fun setOnMovieClickListener(holder: MoviesViewHolder, movie: MovieNetwork) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()
            clickListener(movie.id)
        }
    }
}