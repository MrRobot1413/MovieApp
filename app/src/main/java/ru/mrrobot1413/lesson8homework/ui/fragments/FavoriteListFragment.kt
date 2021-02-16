package ru.mrrobot1413.lesson8homework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.FavoriteListAdapter
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.viewModels.FavoriteListViewModel

class FavoriteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noMoviesSign: TextView
    private lateinit var adapter: FavoriteListAdapter
    private lateinit var favoriteListViewModel: FavoriteListViewModel

    companion object {

        fun newInstance(): FavoriteListFragment {
            val args = Bundle()

            val fragment = FavoriteListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initFields(view)
        initRecycler()
    }

    private fun initFields(view: View) {
        noMoviesSign = view.findViewById(R.id.txt_no_movie)
        noMoviesSign.visibility = View.VISIBLE
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun initViewModel(){
        favoriteListViewModel = ViewModelProvider(this).get(FavoriteListViewModel::class.java)

        favoriteListViewModel.getMovies().observe(viewLifecycleOwner, {
            adapter.setMovies(it)
        })
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FavoriteListAdapter(noMoviesSign) {
            (activity as? MovieClickListener)?.onClick(it)
        }

        recyclerView.adapter = adapter
    }
}