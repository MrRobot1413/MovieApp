package ru.mrrobot1413.movieapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.adapters.WatchLaterListAdapter
import ru.mrrobot1413.movieapp.databinding.FragmentWatchLaterBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.viewModels.FavoriteListViewModel
@AndroidEntryPoint
class WatchLaterFragment : Fragment() {

    private val adapter by lazy {
        WatchLaterListAdapter { movie: Movie ->
            (activity as? MovieClickListener)?.openDetailsFragment(movie.id, 3)
        }
    }
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private var binding: FragmentWatchLaterBinding? = null

    companion object {
        private const val RECYCLER_VIEW_SAVE_STATE = "recycler_view_state"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(RECYCLER_VIEW_SAVE_STATE,
            binding?.recyclerView?.layoutManager?.onSaveInstanceState())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_watch_later, container, false)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        lifecycleScope.launchWhenStarted {
            favoriteListViewModel.watchLaterMovies.collectLatest {
                if (it.isNullOrEmpty()) {
                    adapter.setMovies(mutableListOf())
                    binding?.txtNoMovie?.visibility = View.VISIBLE
                } else {
                    adapter.setMovies(it)
                    binding?.txtNoMovie?.visibility = View.GONE
                }
            }
        }

        favoriteListViewModel.getWatchLaterList()
    }

    override fun onResume() {
        super.onResume()

        (activity as MovieClickListener).showBottomNav()
    }

    private fun initFields() {
        binding?.txtNoMovie?.visibility = View.VISIBLE
        initRecycler()
    }

    private fun initRecycler() {
        binding?.recyclerView?.layoutManager = GridLayoutManager(context, 2)

        adapter.setMovies(mutableListOf())

        binding?.recyclerView?.adapter = adapter
    }
}