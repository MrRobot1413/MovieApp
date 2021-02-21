package ru.mrrobot1413.lesson8homework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.viewHolders.SeriesViewHolder

class SeriesAdapter(
    private var series: MutableList<Series>,
    private val clickListener: (series: Series) -> Unit
) :
    RecyclerView.Adapter<SeriesViewHolder>() {

    fun appendMovies(movies: List<Series>) {
        this.series.addAll(movies)
        notifyItemRangeInserted(
            this.series.size,
            movies.size - 1
        )
    }

    override fun getItemCount() = series.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return SeriesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {

        holder.bind(series[position])
        setOnDetailsClickListener(holder, series[position])
    }

    private fun setOnDetailsClickListener(holder: SeriesViewHolder, series: Series) {
        holder.holder.setOnClickListener {
            notifyDataSetChanged()

            clickListener(series)
        }
    }
}