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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_favorite.*
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.MovieDetailResponse
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository
import ru.mrrobot1413.lesson8homework.ui.MainActivity
import ru.mrrobot1413.lesson8homework.viewModels.FavoriteListViewModel
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class DetailsFragment : Fragment() {

    private lateinit var imageBackdrop: ImageView
    private lateinit var txtDescr: TextView
    private lateinit var txtRating: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtLanguage: TextView
    private lateinit var txtTime: TextView
    private lateinit var txtRevenue: TextView
    private lateinit var inviteText: String
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fabAddToFavorite: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private val favoriteListRepository by lazy {
        FavoriteListRepository.getInstance()
    }
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private var isAddedToFavorite = false

    companion object {

        private const val MOVIE = "movie"


        fun newInstance(movie: Movie): DetailsFragment {
            val args = Bundle()
            args.putParcelable(MOVIE, movie)

            val fragment = DetailsFragment()
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

        val movie = arguments?.getParcelable<Movie>(MOVIE)

        val movieFromDb = favoriteListRepository.selectById(arguments?.getParcelable<Movie>(MOVIE)?.id!!)
        if (movieFromDb != null) {
            if(movieFromDb.liked){
                setIconLiked()
                isAddedToFavorite = true
            } else{
                setIconUnliked()
                isAddedToFavorite = false
            }
        } else{
            setIconUnliked()
            isAddedToFavorite = false
        }

        if (movie != null) {
            moviesViewModel.getMovieDetails(movie.id)
        }

        moviesViewModel.movieDetailResponse.observe(viewLifecycleOwner, {
            setContent(it)
        })

        (activity as MainActivity?)!!.setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        setOnFabClickListener(movie)
    }


    private fun setOnFabClickListener(movie: Movie?) {
        fabAddToFavorite.setOnClickListener {
            if (movie != null) {
                if (isAddedToFavorite) {
                    isAddedToFavorite = false

                    movie.liked = false
                    favoriteListRepository.delete(movie)

                    setIconUnliked()

                } else {
                    isAddedToFavorite = true

                    movie.liked = true
                    favoriteListRepository.likeMovie(movie)

                    setIconLiked()
                }
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
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        toolbar.inflateMenu(R.menu.invite_menu)
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
        txtTime = view.findViewById(R.id.txt_time)
        txtRevenue = view.findViewById(R.id.txt_revenue)
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout)
        fabAddToFavorite = view.findViewById(R.id.fab_add_to_favorite)
        progressBar = view.findViewById(R.id.progress)
    }

    @SuppressLint("SetTextI18n")
    private fun setContent(movie: MovieDetailResponse) {

        context?.let {
            Glide.with(it)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
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

        collapsingToolbarLayout.title = movie.title
        txtRating.text = movie.rating.toString()
        txtDescr.text = movie.overview
        txtDate.text = movie.releaseDate
        txtLanguage.text = movie.language
        txtTime.text = movie.time.toString() + " " + getString(R.string.min)
        txtRevenue.text = String.format("%.2fM", movie.revenue / 1000000.0) + " $"
        inviteText = getString(R.string.invite_text) + movie.title
    }
}