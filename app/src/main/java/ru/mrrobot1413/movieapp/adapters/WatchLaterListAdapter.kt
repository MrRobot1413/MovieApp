package ru.mrrobot1413.movieapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.viewHolders.WatchLaterListViewHolder

class WatchLaterListAdapter(
    private val clickListener: (movie: Movie, holder: RelativeLayout) -> Unit
) :
    RecyclerView.Adapter<WatchLaterListViewHolder>() {

    private lateinit var moviesList: List<Movie>

    fun setMovies(moviesList: List<Movie>){
        this.moviesList = moviesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = moviesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchLaterListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_watch_later, parent, false)
        return WatchLaterListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WatchLaterListViewHolder, position: Int) {

        holder.bind(moviesList[position])
        setOnDetailsClickListener(holder, moviesList[position])
    }

    private fun setOnDetailsClickListener(holder: WatchLaterListViewHolder, movie: Movie) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()
            clickListener(movie, holder.relative)
        }
    }
}