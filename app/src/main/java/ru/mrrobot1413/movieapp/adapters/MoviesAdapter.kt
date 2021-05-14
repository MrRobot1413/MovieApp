package ru.mrrobot1413.movieapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.viewHolders.MoviesViewHolder

class MoviesAdapter(
    private val clickListener: (id: Int) -> Unit
) :
    PagingDataAdapter<MovieNetwork, MoviesViewHolder>(DIFF_UTIL) {

    companion object{
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<MovieNetwork>(){
            override fun areContentsTheSame(oldItem: MovieNetwork, newItem: MovieNetwork) = oldItem.id == newItem.id

            override fun areItemsTheSame(oldItem: MovieNetwork, newItem: MovieNetwork) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MoviesViewHolder(
            layoutInflater.inflate(R.layout.movie_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item!!)
        setOnMovieClickListener(holder, item)
    }

    private fun setOnMovieClickListener(holder: MoviesViewHolder, movie: MovieNetwork) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()
            clickListener(movie.id)
        }
    }
}