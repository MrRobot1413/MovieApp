package ru.mrrobot1413.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers.io
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.adapters.FavoriteListAdapter
import ru.mrrobot1413.movieapp.databinding.FragmentFavoriteBinding
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.model.Movie
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.viewModels.FavoriteListViewModel
import java.lang.Exception

class FavoriteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var txtNoMovie: TextView
    private val adapter by lazy {
        FavoriteListAdapter { id: Int ->
            (activity as? MovieClickListener)?.openDetailsFragment(id, 1)
        }
    }
    private val favoriteListViewModel by lazy {
        ViewModelProvider(this).get(FavoriteListViewModel::class.java)
    }
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RxJavaPlugins.setErrorHandler {
            Log.d("ERIEI", it.message.toString())
        }
        initFields()

        favoriteListViewModel.getFavoriteMovies()
            .subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                if (result.isNullOrEmpty()) {
                    binding.txtNoMovie.visibility = View.VISIBLE
                } else {
                    adapter.setMovies(result)
                    binding.txtNoMovie.visibility = View.GONE
                }
                Log.d("MOVIEZCV", result.toString())
            }, {
                binding.txtNoMovie.visibility = View.VISIBLE
            })

        initRecycler()
    }

    override fun onResume() {
        super.onResume()

        (activity as MovieClickListener).showBottomNav()
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
}