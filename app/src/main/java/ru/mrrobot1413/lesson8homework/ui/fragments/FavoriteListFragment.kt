package ru.mrrobot1413.lesson8homework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_favorite.*
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.FavoriteListAdapter
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.repositories.FavoriteListRepository

class FavoriteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noMoviesSign: TextView
    private val adapter by lazy {
        FavoriteListAdapter(noMoviesSign) {
            (activity as? MovieClickListener)?.onClick(it)
        }
    }
    private val favoriteListRepository by lazy {
        FavoriteListRepository.getInstance()
    }

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

        initFields(view)
        initRecycler()

        showNoMoviesSign()

        relative.setOnRefreshListener {
            adapter.setMovies(favoriteListRepository.selectAll())
            relative.isRefreshing = false
            showNoMoviesSign()
        }
    }

    private fun initFields(view: View) {
        noMoviesSign = view.findViewById(R.id.txt_no_movie)
        noMoviesSign.visibility = View.VISIBLE
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun initRecycler() {
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        adapter.setMovies(favoriteListRepository.selectAll())

        recyclerView.adapter = adapter
    }



    private fun showNoMoviesSign(){
        if(favoriteListRepository.getMoviesCount() != 0){
            noMoviesSign.visibility = View.GONE
        } else{
            noMoviesSign.visibility = View.VISIBLE
        }
    }
}