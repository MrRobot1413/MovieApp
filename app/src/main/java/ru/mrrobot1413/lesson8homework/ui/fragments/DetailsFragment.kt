package ru.mrrobot1413.lesson8homework.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.like.LikeButton
import com.like.OnLikeListener
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.ui.MainActivity
import ru.mrrobot1413.lesson8homework.viewHolders.MoviesViewHolder
import ru.mrrobot1413.lesson8homework.viewModels.FavoriteListViewModel

class DetailsFragment : Fragment() {

    private lateinit var imageBackdrop: ImageView
    private lateinit var txtActors: TextView
    private lateinit var txtDescr: TextView
    private lateinit var txtRating: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtTime: TextView
    private lateinit var txtRestriction: TextView
    private lateinit var inviteText: String
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fabAddToFavorite: FloatingActionButton
    private var isAddedToFavorite = false

    companion object {

        private const val MOVIE = "movie"
        private const val WHERE_CAME_FROM = "WHERE_CAME_FROM"

        fun newInstance(movie: Movie, whereCameFrom: String): DetailsFragment {
            val args = Bundle()
            args.putParcelable(MOVIE, movie)
            args.putString(WHERE_CAME_FROM, whereCameFrom)

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

        val movie = arguments?.getParcelable<Movie>(MOVIE)

        initFields(view)
        movie?.let { setContent(it) }

        (activity as MainActivity?)!!.setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
            val frag = FavoriteListFragment()
            frag.onViewCreated(view, savedInstanceState)
        }

        if (arguments?.getString(WHERE_CAME_FROM).equals(MainActivity.MAIN_ACTIVITY)) {
            if (movie!!.liked) {
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

        setOnFabClickListener(movie)
    }

    private fun setOnFabClickListener(movie: Movie?) {
        fabAddToFavorite.setOnClickListener {
            if (movie != null) {
                if (isAddedToFavorite) {
                    isAddedToFavorite = false

                    movie.liked = false
                    DataStorage.favoriteList.remove(movie)

                    setIconUnliked()
                } else {
                    isAddedToFavorite = true

                    movie.liked = true
                    DataStorage.favoriteList.add(movie)

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
        txtActors = view.findViewById(R.id.txt_actors)
        txtRating = view.findViewById(R.id.txt_rating)
        txtDate = view.findViewById(R.id.txt_date)
        txtCountry = view.findViewById(R.id.txt_country)
        txtTime = view.findViewById(R.id.txt_time)
        txtRestriction = view.findViewById(R.id.txt_age)
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout)
        fabAddToFavorite = view.findViewById(R.id.fab_add_to_favorite)
    }

    @SuppressLint("SetTextI18n")
    private fun setContent(movie: Movie) {
        imageBackdrop.setImageDrawable(ContextCompat.getDrawable(context!!, movie.movieImage))

        val movieName = getText(movie.movieName)
        collapsingToolbarLayout.title = movieName
        txtRating.text = getString(movie.movieRating)
        txtDescr.text = getString(movie.movieDescr)
        txtActors.text = getString(movie.movieActors)
        txtDate.text = getString(R.string.release_date) + " " + getString(movie.movieReleaseDate)
        txtCountry.text = getString(R.string.country) + " " + getString(movie.movieCountry)
        txtTime.text = getString(R.string.time) + " " + getString(movie.movieTime)
        txtRestriction.text =
            getString(R.string.age_restrictions) + " " + getString(movie.movieRestrictions)
        inviteText = getString(movie.movieInviteText)
    }
}