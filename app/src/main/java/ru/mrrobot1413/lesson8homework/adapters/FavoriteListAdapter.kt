package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.MovieDetailed
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository

class FavoriteListAdapter(
    private val clickListener: (movie: MovieDetailed) -> Unit
) :
    RecyclerView.Adapter<MoviesViewHolder>() {

    private lateinit var moviesList: List<MovieDetailed>
    private val favoriteListRepository by lazy {
        FavoriteListRepository.getInstance()
    }

    fun setMovies(moviesList: List<MovieDetailed>){
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

    private fun setOnDetailsClickListener(holder: MoviesViewHolder, movie: MovieDetailed) {
        holder.holder.setOnClickListener {

            notifyDataSetChanged()

            clickListener(movie)
        }
    }
}