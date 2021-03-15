package ru.mrrobot1413.lesson8homework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_favorite.*
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.FavoriteListAdapter
import ru.mrrobot1413.lesson8homework.databinding.FragmentFavoriteBinding
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository
import ru.mrrobot1413.lesson8homework.viewModels.FavoriteListViewModel

class FavoriteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var txtNoMovie: TextView
    private val adapter by lazy {
        FavoriteListAdapter {
            (activity as? MovieClickListener)?.onClick(it)
//            val navController = findNavController()
//            val bundle = Bundle()
//            bundle.putParcelable(MOVIE, it)
//            navController.navigate(R.id.detailsFragment, bundle)
        }
    }
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private lateinit var binding: FragmentFavoriteBinding

    companion object {
        private const val MOVIE = "movie"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields()

        favoriteListViewModel.getMovies().observe(viewLifecycleOwner, {
            adapter.setMovies(it)
            showNoMoviesSign()
        })

        initRecycler()
    }

    private fun initFields() {
        txtNoMovie = binding.txtNoMovie
        binding.txtNoMovie.visibility = View.VISIBLE
        recyclerView = binding.recyclerView
    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        adapter.setMovies(mutableListOf())

        binding.recyclerView.adapter = adapter
    }

    private fun showNoMoviesSign(){
        if(favoriteListViewModel.getMoviesCount() != 0){
            binding.txtNoMovie.visibility = View.GONE
        } else{
            binding.txtNoMovie.visibility = View.VISIBLE
        }
    }
}