package ru.mrrobot1413.movieapp.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.oshi.libsearchtoolbar.SearchAnimationToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.adapters.MoviesAdapter
import ru.mrrobot1413.movieapp.databinding.FragmentHomeBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel


class HomeFragment : Fragment(), SearchAnimationToolbar.OnSearchQueryChangedListener {

    private val adapter by lazy {
        MoviesAdapter(mutableListOf()) { id: Int ->
            (activity as MovieClickListener).openDetailsFragment(id, 1)
        }
    }
    private lateinit var linearLayoutManager: GridLayoutManager
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private var moviesPage = 1
    lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        moviesViewModel.movies.observe(viewLifecycleOwner, {
            runLayoutAnimation(binding.recyclerView,
                binding.recyclerView.layoutManager as GridLayoutManager)
            binding.recyclerView.visibility = View.VISIBLE
            adapter.setMovies(it)
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(viewLifecycleOwner, {
            showSnackbar(it)
            binding.refreshLayout.isRefreshing = false
//            moviesViewModel.selectAll().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result ->
//                    val movies = mutableListOf<MovieNetwork>()
//                    for(i in 0..result.size){
//                        movies[i] = MovieNetwork(
//                            result[i].id,
//                            result[i].title,
//                            result[i].overview,
//                            result[i].posterPath,
//                            result[i].rating,
//                            result[i].releaseDate,
//                            result[i].time,
//                            result[i].language
//                        )
//                    }
//                    adapter.setMovies(movies)
//                }, {})
        })

        moviesViewModel.getPopularMovies(moviesPage, getString(R.string.no_connection),
            getString(R.string.error_loading_movies))

        binding.toolbar.setSupportActionBar(activity as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as MovieClickListener).showBottomNav()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun showTopRatedMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getTopRatedMovies(1, getString(R.string.no_connection),
            getString(R.string.error_loading_movies))
    }

    private fun showPopularMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
            getString(R.string.error_loading_movies))
    }

    private fun showSnackbar(text: String) {
        view?.let {
            Snackbar.make(it, text, Snackbar.LENGTH_INDEFINITE).setAnchorView(R.id.bottom)
                .setAction(getString(R.string.retry)) {
                    moviesViewModel.getPopularMovies(
                        moviesPage,
                        getString(R.string.no_connection),
                        getString(R.string.error_loading_movies)
                    )
                }.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                .show()
        }
    }

    private fun showGenreSearchArea() {
        val transition: Transition = Explode()
        transition.duration = 700
        transition.addTarget(binding.genreSearch)

        TransitionManager.beginDelayedTransition(binding.container, transition)
        if (binding.genreSearch.isVisible) {
            binding.genreSearch.visibility = View.GONE
        } else {
            binding.genreSearch.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> toolbar.onSearchIconClick()
            R.id.popular -> showPopularMovies()
            R.id.top_rated -> showTopRatedMovies()
            R.id.search_by_genre -> showGenreSearchArea()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFields() {
        linearLayoutManager =
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(requireContext(), 3)
            } else {
                GridLayoutManager(requireContext(), 2)
            }

        initRecycler()
        initChips()

        binding.toolbar.setOnSearchQueryChangedListener(this)

        binding.refreshLayout.setOnRefreshListener {
            adapter.setMoviesFromMenu(mutableListOf())
            moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                getString(R.string.error_loading_movies))
            binding.refreshLayout.isRefreshing = false

            binding.group.clearCheck()
        }

        binding.refreshLayout.isEnabled = true
    }

    private fun attachPopularMoviesOnScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = linearLayoutManager.itemCount
                val visibleItemCount = linearLayoutManager.childCount
                val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (moviesPage < 500) {
                        recyclerView.removeOnScrollListener(this)
                        moviesPage++
                        moviesViewModel.getPopularMovies(moviesPage,
                            getString(R.string.no_connection),
                            getString(R.string.error_loading_movies))
                        attachPopularMoviesOnScrollListener()
                    }
                }
            }
        })
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView, linearLayout: GridLayoutManager) {
        if (linearLayout.childCount <= 4 && linearLayout.itemCount <= 4) {
            val context = recyclerView.context
            val controller: LayoutAnimationController =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
            recyclerView.layoutAnimation = controller
            recyclerView.adapter!!.notifyDataSetChanged()
            recyclerView.scheduleLayoutAnimation()
        }
    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager = linearLayoutManager

        binding.recyclerView.adapter = adapter

        attachPopularMoviesOnScrollListener()
    }

    override fun onSearchCollapsed() {

    }

    override fun onSearchQueryChanged(query: String?) {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.searchMovie(1, query, getString(R.string.no_connection)
        )
    }

    override fun onSearchExpanded() {
    }

    override fun onSearchSubmitted(query: String?) {
        val view = activity?.currentFocus
        if (view != null) {
            val im: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initChips() {
        binding.chipAction.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipAction.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    28,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipAdventure.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipAdventure.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    12,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipAnimation.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipAnimation.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    16,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipComedy.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipComedy.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    35,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipCrime.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipCrime.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    80,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipDocumentary.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipDocumentary.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    99,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipDrama.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipDrama.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    18,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipFamily.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipFamily.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    10751,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipFantasy.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipFantasy.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    14,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipHistory.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipHistory.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    36,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipHorror.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipHorror.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    27,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipMusic.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipMusic.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    10402,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipMystery.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipMystery.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    9648,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipRomance.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipRomance.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    10749,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipScienceFiction.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipScienceFiction.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    878,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipThriller.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipThriller.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    53,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipWar.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipWar.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    10752,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }

        binding.chipWestern.setOnClickListener {
            adapter.setMoviesFromMenu(mutableListOf())
            if (binding.chipWar.isChecked) {
                moviesViewModel.searchMovieByGenre(
                    53,
                    getString(R.string.no_connection),
                    getString(R.string.error_loading_movies)
                )
            } else {
                moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                    getString(R.string.error_loading_movies))
            }
        }
    }
}