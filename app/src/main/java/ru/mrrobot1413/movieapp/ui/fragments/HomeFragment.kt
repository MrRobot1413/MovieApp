package ru.mrrobot1413.movieapp.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
            runLayoutAnimation(binding.recyclerView, binding.recyclerView.layoutManager as GridLayoutManager)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> toolbar.onSearchIconClick()
            R.id.popular -> showPopularMovies()
            R.id.top_rated -> showTopRatedMovies()
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

        binding.toolbar.setOnSearchQueryChangedListener(this)

        binding.refreshLayout.setOnRefreshListener {
            adapter.setMoviesFromMenu(mutableListOf())
            moviesViewModel.getPopularMovies(1, getString(R.string.no_connection),
                getString(R.string.error_loading_movies))
            binding.refreshLayout.isRefreshing = false
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
        if(linearLayout.childCount <= 4 && linearLayout.itemCount <= 4){
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
        moviesViewModel.searchMovie(1, query, getString(R.string.no_connection),
            getString(R.string.error_loading_movies))
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
}