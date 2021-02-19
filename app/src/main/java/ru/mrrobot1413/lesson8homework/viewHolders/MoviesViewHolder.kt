package ru.mrrobot1413.lesson8homework.viewHolders

import android.annotation.SuppressLint
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.R

class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.txt_name)
    var image: ImageView = itemView.findViewById(R.id.image_main)
    var holder: RelativeLayout = itemView.findViewById(R.id.holder)

    @SuppressLint("SetTextI18n")
    fun bind(movie: Movie) {
        title.text = movie.title
        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .transform(CenterCrop())
            .error(ContextCompat.getDrawable(itemView.context, R.drawable.ic_round_error_24))
            .into(image)
    }

}