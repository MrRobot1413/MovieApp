package ru.mrrobot1413.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.transition.Explode
import android.transition.Transition
import android.transition.TransitionManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.oshi.libsearchtoolbar.SearchAnimationToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.adapters.MoviesAdapter
import ru.mrrobot1413.movieapp.databinding.FragmentHomeBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(), SearchAnimationToolbar.OnSearchQueryChangedListener {

    private val adapter by lazy {
        MoviesAdapter(mutableListOf()) { id: Int ->
            (activity as MovieClickListener).openDetailsFragment(id, 1)
        }
    }
    private lateinit var layoutManager: GridLayoutManager

    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var snackbar: Snackbar

    companion object {
        private const val RECYCLER_VIEW_SAVE_STATE = "recycler_view_state"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        initFields()

        moviesViewModel.getPopularMovies(1,
            getString(R.string.error_loading_movies))

        binding.toolbar.setSupportActionBar(activity as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    private fun setObservers(){
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                moviesViewModel.isVisible.collectLatest {
                    binding.progressBar.isVisible = it
                }
            }
        }

        lifecycleScope.launchWhenStarted {

            moviesViewModel.movies.collectLatest {
                snackbar.dismiss()
                binding.errorAnimation.isVisible = false
                binding.recyclerView.visibility = View.VISIBLE
                adapter.setMovies(it)
                binding.refreshLayout.isRefreshing = false
            }

            moviesViewModel.error.collectLatest{
                adapter.setMoviesFromMenu(mutableListOf())
                binding.progressBar.isVisible = false
                binding.errorAnimation.isVisible = true
                binding.errorAnimation.playAnimation()
                showSnackbar()
                binding.refreshLayout.isRefreshing = false
            }
        }

    }

    private fun attachRecyclerScroll() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (moviesViewModel.pages < 500) {
                        recyclerView.removeOnScrollListener(this)
                        moviesViewModel.pages++
                        moviesViewModel.getPopularMovies(moviesViewModel.pages,
                            getString(R.string.error_occurred))
                        attachRecyclerScroll()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MovieClickListener).showBottomNav()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(RECYCLER_VIEW_SAVE_STATE,
            binding.recyclerView.layoutManager?.onSaveInstanceState())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun showTopRatedMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getTopRatedMovies(1, getString(R.string.error_occurred))
        binding.recyclerView.scrollToPosition(0)
    }

    private fun showPopularMovies() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getPopularMovies(1, getString(R.string.error_occurred))
        binding.recyclerView.scrollToPosition(0)
    }

    @SuppressLint("ShowToast")
    private fun showSnackbar() {
        view?.let {
            snackbar.show()
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
            R.id.search -> binding.toolbar.onSearchIconClick()
            R.id.popular -> showPopularMovies()
            R.id.top_rated -> showTopRatedMovies()
            R.id.search_by_genre -> showGenreSearchArea()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFields() {
        layoutManager =
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(requireContext(), 3)
            } else {
                GridLayoutManager(requireContext(), 2)
            }

        initRecycler()
        initChips()

        binding.toolbar.setOnSearchQueryChangedListener(this)


        binding.apply {
            snackbar = Snackbar.make(recyclerView,
                getString(R.string.error_occurred),
                Snackbar.LENGTH_INDEFINITE)

            refreshLayout.setOnRefreshListener {
                moviesViewModel.getPopularMovies(1, getString(R.string.error_occurred))
                refreshLayout.isRefreshing = false
                recyclerView.scrollToPosition(0)
                group.clearCheck()
            }

            refreshLayout.isEnabled = true
        }
    }

    private fun initRecycler() {
        binding.apply {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
        attachRecyclerScroll()
    }

    override fun onSearchCollapsed() {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getPopularMovies(
            1,
            getString(R.string.error_occurred)
        )
    }

    override fun onSearchQueryChanged(query: String?) {
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.searchMovie(
            1,
            query,
            getString(R.string.error_occurred)
        )
    }

    override fun onSearchExpanded() {}

    override fun onSearchSubmitted(query: String?) {
        val view = activity?.currentFocus
        if (view != null) {
            val im: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setChipListener(isChecked: Boolean, id: Int) {
        adapter.setMoviesFromMenu(mutableListOf())
        if (isChecked) {
            moviesViewModel.searchMovieByGenre(
                id,
                getString(R.string.error_occurred)
            )
        } else {
            moviesViewModel.getPopularMovies(1, getString(R.string.error_occurred))
        }
        binding.recyclerView.scrollToPosition(0)
    }

    private fun initChips() {
        val clickListener = View.OnClickListener {
            when (it.id) {
                binding.chipAction.id -> setChipListener(binding.chipAction.isChecked, 28)
                binding.chipAdventure.id -> setChipListener(binding.chipAdventure.isChecked, 12)
                binding.chipAnimation.id -> setChipListener(binding.chipAnimation.isChecked, 16)
                binding.chipComedy.id -> setChipListener(binding.chipComedy.isChecked, 35)
                binding.chipCrime.id -> setChipListener(binding.chipCrime.isChecked, 80)
                binding.chipDocumentary.id -> setChipListener(binding.chipDocumentary.isChecked, 99)
                binding.chipDrama.id -> setChipListener(binding.chipDrama.isChecked, 18)
                binding.chipFamily.id -> setChipListener(binding.chipFamily.isChecked, 10751)
                binding.chipFantasy.id -> setChipListener(binding.chipFantasy.isChecked, 14)
                binding.chipHistory.id -> setChipListener(binding.chipHistory.isChecked, 36)
                binding.chipHorror.id -> setChipListener(binding.chipHorror.isChecked, 27)
                binding.chipMusic.id -> setChipListener(binding.chipMusic.isChecked, 10402)
                binding.chipMystery.id -> setChipListener(binding.chipMystery.isChecked, 9648)
                binding.chipRomance.id -> setChipListener(binding.chipRomance.isChecked, 10749)
                binding.chipScienceFiction.id -> setChipListener(binding.chipScienceFiction.isChecked,
                    878)
                binding.chipThriller.id -> setChipListener(binding.chipThriller.isChecked, 53)
                binding.chipWar.id -> setChipListener(binding.chipWar.isChecked, 10752)
                binding.chipWestern.id -> setChipListener(binding.chipWestern.isChecked, 37)
            }
        }

        binding.chipAction.setOnClickListener(clickListener)
        binding.chipAdventure.setOnClickListener(clickListener)
        binding.chipAnimation.setOnClickListener(clickListener)
        binding.chipComedy.setOnClickListener(clickListener)
        binding.chipCrime.setOnClickListener(clickListener)
        binding.chipDocumentary.setOnClickListener(clickListener)
        binding.chipDrama.setOnClickListener(clickListener)
        binding.chipFamily.setOnClickListener(clickListener)
        binding.chipFantasy.setOnClickListener(clickListener)
        binding.chipHistory.setOnClickListener(clickListener)
        binding.chipHorror.setOnClickListener(clickListener)
        binding.chipMusic.setOnClickListener(clickListener)
        binding.chipMystery.setOnClickListener(clickListener)
        binding.chipRomance.setOnClickListener(clickListener)
        binding.chipScienceFiction.setOnClickListener(clickListener)
        binding.chipThriller.setOnClickListener(clickListener)
        binding.chipWar.setOnClickListener(clickListener)
    }
}