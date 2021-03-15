package ru.mrrobot1413.lesson8homework.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oshi.libsearchtoolbar.SearchAnimationToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel
import ru.mrrobot1413.lesson8homework.databinding.FragmentHomeBinding;
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener

class HomeFragment: Fragment(), SearchAnimationToolbar.OnSearchQueryChangedListener  {

    private val adapter by lazy {
        MoviesAdapter(mutableListOf()){
            (activity as? MovieClickListener)?.onClick(it)
        }
    }
    private lateinit var linearLayoutManager: GridLayoutManager
    private val moviesViewModel: MoviesViewModel by navGraphViewModels(R.id.nav_graph) {
//        ViewModelProvider(this).get(MoviesViewModel::class.java)
        SavedStateViewModelFactory(requireActivity().application, requireParentFragment())
    }
    private var moviesPage = 1
    lateinit var binding: FragmentHomeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moviesViewModel.movies.observe(viewLifecycleOwner, {
            binding.recyclerView.visibility = View.VISIBLE
            adapter.setMovies(it)
            deleteNoConnectionSign()
            binding.refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(viewLifecycleOwner, {
            // повторить попытку подключения через 5 сек
            Handler(Looper.getMainLooper()).postDelayed({
                moviesViewModel.getPopularMovies(
                    moviesPage
                )
            }, 5000)
            onError(it)
            binding.refreshLayout.isRefreshing = false
        })

        initFields()
        binding.toolbar.setSupportActionBar(activity as AppCompatActivity)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        moviesViewModel.getPopularMovies(
            moviesPage
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun showTopRatedMovies(){
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getTopRatedMovies(1)
    }

    private fun showPopularMovies(){
        adapter.setMoviesFromMenu(mutableListOf())
        moviesViewModel.getPopularMovies(1)
    }

    private fun showSearchView(){
        toolbar.onSearchIconClick()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
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

        deleteNoConnectionSign()

        binding.toolbar.setOnSearchQueryChangedListener(this)

        binding.refreshLayout.setOnRefreshListener {
            moviesViewModel.getPopularMovies(
                moviesPage
            )
            binding.refreshLayout.isRefreshing = false
        }


        binding.refreshLayout.isEnabled = true
    }

    private fun onError(text: String) {
        binding.txtNoConnection.text = text
        showNoConnectionSign()
        binding.recyclerView.visibility = View.GONE
    }

    private fun showNoConnectionSign() {
        binding.txtNoConnection.visibility = View.VISIBLE
        binding.imageNoConnection.visibility = View.VISIBLE
    }

    private fun deleteNoConnectionSign() {
        binding.txtNoConnection.visibility = View.GONE
        binding.imageNoConnection.visibility = View.GONE
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
        adapter.setMovies(mutableListOf())
        moviesViewModel.searchMovie(1, query!!)
    }

    override fun onSearchExpanded() {

    }

    override fun onSearchSubmitted(query: String?) {
        val view = activity?.currentFocus
        if(view != null){
            val im: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}