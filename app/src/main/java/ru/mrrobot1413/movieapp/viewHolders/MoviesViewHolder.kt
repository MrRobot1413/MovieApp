package ru.mrrobot1413.movieapp.viewHolders

import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork

class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.txt_name)
    var image: ImageView = itemView.findViewById(R.id.image_main)
    var holder: RelativeLayout = itemView.findViewById(R.id.holder)
    var relative: RelativeLayout = itemView.findViewById(R.id.relative)

    fun bind(movie: MovieNetwork) {
        image.clipToOutline = true
        title.text = movie.title
        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .transform(CenterInside())
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(ContextCompat.getDrawable(itemView.context, R.drawable.ic_round_error_24))
            .into(image)
    }

    fun bind(movie: Movie) {
        image.clipToOutline = true
        title.text = movie.title
        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .transform(CenterInside())
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(ContextCompat.getDrawable(itemView.context, R.drawable.ic_round_error_24))
            .into(image)
    }
}