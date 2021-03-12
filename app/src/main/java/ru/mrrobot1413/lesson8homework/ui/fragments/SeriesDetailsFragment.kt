package ru.mrrobot1413.lesson8homework.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository
import ru.mrrobot1413.lesson8homework.ui.MainActivity

class SeriesDetailsFragment : Fragment() {

    private lateinit var imageBackdrop: ImageView
    private lateinit var txtDescr: TextView
    private lateinit var txtRating: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtLanguage: TextView
    private lateinit var inviteText: String
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fabAddToFavorite: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private var isAddedToFavorite = false
    private val favoriteListRepository by lazy {
        FavoriteListRepository.getInstance()
    }

    companion object {

        private const val SERIES = "series"
        private const val WHERE_CAME_FROM = "WHERE_CAME_FROM"

        fun newInstance(series: Series, whereCameFrom: String): SeriesDetailsFragment {
            val args = Bundle()
            args.putParcelable(SERIES, series)
            args.putString(WHERE_CAME_FROM, whereCameFrom)

            val fragment = SeriesDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields(view)
        val series = arguments?.getParcelable<Series>(SERIES)
        series?.let { setContent(it) }

        (activity as MainActivity?)!!.setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        if (arguments?.getString(WHERE_CAME_FROM).equals(MainActivity.MAIN_ACTIVITY)) {
            if (series!!.liked) {
                setIconLiked()
                isAddedToFavorite = true
            }
        } else {
            fabAddToFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_favorite
                )
            )
            isAddedToFavorite = true
        }

        if (series != null) {
            setOnFabClickListener(series)
        }
    }

    private fun setOnFabClickListener(series: Series) {
        fabAddToFavorite.setOnClickListener {
                if (isAddedToFavorite) {
                    isAddedToFavorite = false

                    series.liked = false
                    //favoriteListRepository.delete(series)

                    setIconUnliked()

                } else {
                    isAddedToFavorite = true

                    series.liked = true
//                    favoriteListRepository.likeSeries(series)

                    setIconLiked()
                }
        }
    }

    private fun setIconLiked() {
        fabAddToFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_favorite
            )
        )
    }

    private fun setIconUnliked() {
        fabAddToFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_favorite_border
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.invite_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.invite_friend) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "The movie ${collapsingToolbarLayout.title}")
            sendIntent.putExtra(Intent.EXTRA_TEXT, inviteText)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFields(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        imageBackdrop = view.findViewById(R.id.image_backdrop)
        txtDescr = view.findViewById(R.id.txt_descr)
        txtRating = view.findViewById(R.id.txt_rating)
        txtDate = view.findViewById(R.id.txt_date)
        txtLanguage = view.findViewById(R.id.txt_language)
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout)
        fabAddToFavorite = view.findViewById(R.id.fab_add_to_favorite)
        progressBar = view.findViewById(R.id.progress)
    }

    @SuppressLint("SetTextI18n")
    private fun setContent(series: Series) {

        Log.d("series-view", series.toString())
        context?.let {
            Glide.with(it)
                .load("https://image.tmdb.org/t/p/w342${series.posterPath}")
                .transform(CenterCrop())
                .error(ContextCompat.getDrawable(context!!, R.drawable.ic_round_error_24))
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
                .into(imageBackdrop)
        }

        val movieName = series.name
        collapsingToolbarLayout.title = movieName
        txtRating.text = series.rating.toString()
        txtDescr.text = series.overview
        txtDate.text = series.releaseDate
        txtLanguage.text = series.language
    }
}