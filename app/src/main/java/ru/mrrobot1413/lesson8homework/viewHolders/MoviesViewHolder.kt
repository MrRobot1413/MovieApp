package ru.mrrobot1413.lesson8homework.viewHolders

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.MovieDetailed
import ru.mrrobot1413.lesson8homework.model.Series


class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.txt_name)
    var image: ImageView = itemView.findViewById(R.id.image_main)
    var holder: RelativeLayout = itemView.findViewById(R.id.holder)
    var progressBar: ProgressBar = itemView.findViewById(R.id.progress)

    @SuppressLint("SetTextI18n")
    fun bind(movie: MovieDetailed) {
        title.text = movie.title
        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .transform(CenterCrop())
            .error(ContextCompat.getDrawable(itemView.context, R.drawable.ic_round_error_24))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

            })
            .into(image)
    }
}