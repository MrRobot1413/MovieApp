package ru.mrrobot1413.movieapp.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel

class HomeFragment : Fragment(), SearchAnimationToolbar.OnSearchQueryChangedListener {

    private val adapter by lazy {
        MoviesAdapter(mutableListOf()) { id: Int, holder: RelativeLayout ->
            (activity as MovieClickListener).openDetailsFragment(id, holder, 1)
        }
    }

    private lateinit var linearLayoutManager: GridLayoutManager
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private var moviesPage = 1
    lateinit var binding: FragmentHomeBinding
    var parcelable: Parcelable? = null

    companion object{
        const val RECYCLER_VIEW = "rec_view"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        moviesViewModel.movies.observe(viewLifecycleOwner, {
            binding.recyclerView.visibility = View.VISIBLE
            adapter.setMovies(it)
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(viewLifecycleOwner, {
            showSnackbar(it)
            moviesViewModel.selectAll().let { it1 -> adapter.setMovies(it1) }
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.getPopularMovies(moviesPage)

        binding.toolbar.setSupportActionBar(activity as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState?.getParcelable<Parcelable>(RECYCLER_VIEW) != null) {
            parcelable = savedInstanceState.getParcelable(RECYCLER_VIEW)!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(RECYCLER_VIEW, linearLayoutManager.onSaveInstanceState())
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
        moviesViewModel.getTopRatedMovies(1)
    }

    private fun showPopularMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getPopularMovies(1)
    }

    private fun showSearchView() {
        toolbar.onSearchIconClick()
    }

    private fun showSnackbar(text: String) {
        view?.let {
            Snackbar.make(it, text, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry)) {
                moviesViewModel.getPopularMovies(
                    moviesPage
                )
            }.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent)).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> showSearchView()
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
            moviesViewModel.getPopularMovies(
                moviesPage
            )
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
                    recyclerView.removeOnScrollListener(this)
                    moviesPage++
                    moviesViewModel.getPopularMovies(moviesPage)
                    attachPopularMoviesOnScrollListener()
                }
            }
        })
    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager = linearLayoutManager

        binding.recyclerView.adapter = adapter

        attachPopularMoviesOnScrollListener()
    }

    override fun onSearchCollapsed() {

    }

    override fun onSearchQueryChanged(query: String?) {
        if (query != null) {
            if (query.length >= 2) {
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.setMoviesFromMenu(mutableListOf())
                    moviesViewModel.searchMovie(1, query)
                }, 1000)
            }
        }
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