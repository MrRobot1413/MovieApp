package ru.mrrobot1413.lesson8homework.viewHolders

import android.annotation.SuppressLint
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.R

class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtName: TextView = itemView.findViewById(R.id.txt_name)
    var image: ImageView = itemView.findViewById(R.id.image_main)
    var holder: RelativeLayout = itemView.findViewById(R.id.holder)

    @SuppressLint("SetTextI18n")
    fun bind(movie: Movie) {
        val activity = itemView.context
        txtName.text = activity.getString(movie.movieName)
        image.setImageDrawable(movie.movieImage.let {
            ContextCompat.getDrawable(
                itemView.context,
                it
            )
        })
    }

}