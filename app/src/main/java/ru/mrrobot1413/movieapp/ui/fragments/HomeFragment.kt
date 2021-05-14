package ru.mrrobot1413.movieapp.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.transition.Explode
import android.transition.Transition
import android.transition.TransitionManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oshi.libsearchtoolbar.SearchAnimationToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.adapters.MoviesAdapter
import ru.mrrobot1413.movieapp.databinding.FragmentHomeBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.ui.MovieLoadStateAdapter
import ru.mrrobot1413.movieapp.viewModels.MoviesViewModel

class HomeFragment : Fragment(), SearchAnimationToolbar.OnSearchQueryChangedListener {

    private val adapter by lazy {
        MoviesAdapter { id: Int ->
            (activity as MovieClickListener).openDetailsFragment(id, 1)
        }
    }
    private lateinit var linearLayoutManager: GridLayoutManager
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    lateinit var binding: FragmentHomeBinding

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

        initFields()

        moviesViewModel.movies.observe(viewLifecycleOwner, {
            binding.recyclerView.visibility = View.VISIBLE
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(viewLifecycleOwner, {
            showSnackbar(it)
            binding.refreshLayout.isRefreshing = false
        })

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.apply {
                    progressBar.isVisible = loadStates.refresh is LoadState.Loading
                    recyclerView.isVisible = loadStates.refresh is LoadState.NotLoading
                    errorAnimation.isVisible = loadStates.refresh is LoadState.NotLoading
                    if(loadStates.refresh is LoadState.Error){
                        errorAnimation.isVisible = true
                        showSnackbar(getString(R.string.error_occurred))
                    }
                }
            }
        }

        moviesViewModel.getPopularMovies(
            getString(R.string.error_loading_movies))

        binding.toolbar.setSupportActionBar(activity as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as MovieClickListener).showBottomNav()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun showTopRatedMovies() {
        moviesViewModel.getTopRatedMovies(getString(R.string.no_connection))
        binding.recyclerView.scrollToPosition(0)
    }

    private fun showPopularMovies() {
        moviesViewModel.getPopularMovies(getString(R.string.no_connection))
        binding.recyclerView.scrollToPosition(0)
    }

    private fun showSnackbar(text: String) {
        view?.let {
            Snackbar.make(it, text, Snackbar.LENGTH_INDEFINITE).setAnchorView(R.id.bottom)
                .setAction(getString(R.string.retry)) {
                    moviesViewModel.getPopularMovies(
                        getString(R.string.no_connection)
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
            moviesViewModel.getPopularMovies(getString(R.string.no_connection))
            binding.refreshLayout.isRefreshing = false
            binding.recyclerView.scrollToPosition(0)
            binding.group.clearCheck()
        }

        binding.refreshLayout.isEnabled = true
    }

//    private fun runLayoutAnimation(recyclerView: RecyclerView, linearLayout: GridLayoutManager) {
//        if (linearLayout.childCount <= 4 && linearLayout.itemCount <= 4) {
//            val context = recyclerView.context
//            val controller: LayoutAnimationController =
//                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
//            recyclerView.layoutAnimation = controller
//            recyclerView.adapter!!.notifyDataSetChanged()
//            recyclerView.scheduleLayoutAnimation()
//        }
//    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = MovieLoadStateAdapter()
        )
    }

    override fun onSearchCollapsed() {

    }

    override fun onSearchQueryChanged(query: String?) {
        moviesViewModel.searchMovie(
            query,
            getString(R.string.no_connection)
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
        if (isChecked) {
            moviesViewModel.searchMovieByGenre(
                id,
                getString(R.string.error_loading_movies)
            )
        } else {
            moviesViewModel.getPopularMovies(getString(R.string.no_connection))
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