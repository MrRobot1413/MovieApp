package ru.mrrobot1413.movieapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.viewHolders.MoviesViewHolder

class MoviesAdapter(
    moviesSrc: MutableList<MovieNetwork>,
    private val clickListener: (id: Int) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {
    private val list: MutableList<MovieNetwork> = moviesSrc

    fun setMovies(list: List<MovieNetwork>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun setMoviesFromMenu(list: List<MovieNetwork>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MoviesViewHolder(
            layoutInflater.inflate(R.layout.movie_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item)
        setOnMovieClickListener(holder, item)
    }

    private fun setOnMovieClickListener(holder: MoviesViewHolder, movie: MovieNetwork) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()
            clickListener(movie.id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}