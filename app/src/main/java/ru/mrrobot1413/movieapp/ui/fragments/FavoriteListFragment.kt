package ru.mrrobot1413.movieapp.ui.fragments

import android.os.Bundle
import android.os.Parcelable
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
import kotlinx.coroutines.flow.collectLatest
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.adapters.FavoriteListAdapter
import ru.mrrobot1413.movieapp.databinding.FragmentFavoriteBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.viewModels.FavoriteListViewModel

class FavoriteListFragment : Fragment() {

    private val adapter by lazy {
        FavoriteListAdapter { id: Int ->
            (activity as? MovieClickListener)?.openDetailsFragment(id, 1)
        }
    }
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private var binding: FragmentFavoriteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        lifecycleScope.launchWhenStarted {
            favoriteListViewModel.favoriteMovies.collectLatest{ result ->
                if (result.isNullOrEmpty()) {
                    binding?.txtNoMovie?.visibility = View.VISIBLE
                    adapter.setMovies(mutableListOf())
                } else {
                    binding?.txtNoMovie?.visibility = View.GONE
                    adapter.setMovies(result)
                }
            }
        }

        favoriteListViewModel.getFavoriteMovies()
    }

    override fun onResume() {
        super.onResume()

        (activity as MovieClickListener).showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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