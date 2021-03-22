package ru.mrrobot1413.lesson8homework.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.databinding.FragmentDetailsBinding
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.ui.MainActivity
import ru.mrrobot1413.lesson8homework.viewModels.FavoriteListViewModel
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class DetailsFragment : Fragment() {

    private lateinit var inviteText: String
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    lateinit var binding: FragmentDetailsBinding
    private var isAddedToFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition

        val movie = arguments?.getParcelable<Movie>(MainActivity.MOVIE)

        val movieFromDb = movie?.id?.let { favoriteListViewModel.selectById(it) }

        isAddedToFavorite = if (movieFromDb != null) {
            if (movieFromDb.liked) {
                setIconLiked()
                true
            } else {
                setIconUnliked()
                false
            }
        } else {
            setIconUnliked()
            false
        }

        if (movie != null) {
            moviesViewModel.getMovieDetails(movie.id)
            setImage(binding.imageBackdrop, "https://image.tmdb.org/t/p/w342" + movie.posterPath)
        }

        binding.viewModel = moviesViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        (activity as MainActivity?)!!.setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        setOnFabClickListener(movie)
    }

    private fun setOnFabClickListener(movie: Movie?) {
        binding.fabAddToFavorite.setOnClickListener {
            if (movie != null) {
                if (isAddedToFavorite) {
                    isAddedToFavorite = false

                    movie.liked = false
                    favoriteListViewModel.delete(movie)

                    setIconUnliked()
                } else {
                    isAddedToFavorite = true

                    movie.liked = true
                    movie.time = moviesViewModel.movieDetailed.value?.time ?: 0
                    favoriteListViewModel.likeMovie(movie)

                    setIconLiked()
                }
            }
        }
    }

    private fun setImage(view: ImageView, url: String) {
        context?.let {
            Glide.with(it)
                .load(url)
                .transform(CenterCrop())
                .error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_error_24))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }

                })
                .into(view)
        }
    }

    private fun setIconLiked() {
        binding.fabAddToFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite
            )
        )
    }

    private fun setIconUnliked() {
        binding.fabAddToFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite_border
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        binding.toolbar.inflateMenu(R.menu.invite_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.invite_friend) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, binding.collapsingToolbarLayout.title)
            sendIntent.putExtra(Intent.EXTRA_TEXT, inviteText)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}