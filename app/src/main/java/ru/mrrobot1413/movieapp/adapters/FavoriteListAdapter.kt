package ru.mrrobot1413.movieapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.movieapp.viewHolders.MoviesViewHolder
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie

class FavoriteListAdapter(
    private val clickListener: (id: Int, holder: RelativeLayout) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    private lateinit var moviesList: List<Movie>

    fun setMovies(moviesList: List<Movie>){
        this.moviesList = moviesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = moviesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MoviesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {

        holder.bind(moviesList[position])
        setOnDetailsClickListener(holder, moviesList[position])
    }

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: Movie) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()
            clickListener(movie.id, holder.relative)
        }
    }
}