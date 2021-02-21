package ru.mrrobot1413.lesson8homework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.SeriesAdapter
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class SeriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val adapter by lazy {
        SeriesAdapter(mutableListOf()) {
            (activity as? MovieClickListener)?.onClick(it)
        }
    }
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var txtNoConnection: TextView
    private lateinit var imageNoConnection: ImageView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var moviesPage = 1

    companion object {

        fun newInstance(): SeriesFragment {
            val args = Bundle()

            val fragment = SeriesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields(view)
        initRecycler()

        refreshLayout.setOnRefreshListener {
            getPopularSeries()
            refreshLayout.isRefreshing = false
        }
    }

    private fun initFields(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        refreshLayout = view.findViewById(R.id.refresh_layout)
        txtNoConnection = view.findViewById(R.id.txt_no_connection)
        imageNoConnection = view.findViewById(R.id.image_no_connection)
    }

    private fun initRecycler() {
        linearLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = linearLayoutManager

        getPopularSeries()

        recyclerView.adapter = adapter
    }

    private fun getPopularSeries() {
        moviesViewModel.getSeries(
            moviesPage,
            {
                recyclerView.visibility = View.VISIBLE
                adapter.appendMovies(it)
                deleteNoConnectionSign()
                refreshLayout.isRefreshing = false
            },
            {
                onError()
                refreshLayout.isRefreshing = false
            }
        )
        attachPopularMoviesOnScrollListener()
    }

    private fun onError() {
        showNoConnectionSign()
        recyclerView.visibility = View.GONE
    }

    private fun showNoConnectionSign() {
        txtNoConnection.visibility = View.VISIBLE
        imageNoConnection.visibility = View.VISIBLE
    }

    private fun deleteNoConnectionSign() {
        txtNoConnection.visibility = View.GONE
        imageNoConnection.visibility = View.GONE
    }

    private fun attachPopularMoviesOnScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = linearLayoutManager.itemCount
                val visibleItemCount = linearLayoutManager.childCount
                val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    recyclerView.removeOnScrollListener(this)
                    moviesPage++
                    getPopularSeries()
                }
            }
        })
    }
}
