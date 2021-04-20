package ru.mrrobot1413.movieapp.viewHolders

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.model.Movie

class WatchLaterListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView = itemView.findViewById(R.id.txt_name)
    var image: ImageView = itemView.findViewById(R.id.image_main)
    var reminder: TextView = itemView.findViewById(R.id.txt_reminder)
    var holder: RelativeLayout = itemView.findViewById(R.id.holder)
    var relative: RelativeLayout = itemView.findViewById(R.id.relative)
    var progressBar: ProgressBar = itemView.findViewById(R.id.progress)

    fun bind(movie: Movie) {
        image.clipToOutline = true
        title.text = movie.title
        reminder.text = movie.reminder
        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .transform(CenterInside())
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