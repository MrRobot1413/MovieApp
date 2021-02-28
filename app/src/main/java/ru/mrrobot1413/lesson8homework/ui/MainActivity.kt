package ru.mrrobot1413.lesson8homework.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.model.Series
import ru.mrrobot1413.lesson8homework.ui.fragments.DetailsFragment
import ru.mrrobot1413.lesson8homework.ui.fragments.FavoriteListFragment
import ru.mrrobot1413.lesson8homework.ui.fragments.SeriesDetailsFragment
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class MainActivity : AppCompatActivity(), MovieClickListener {

    private lateinit var recyclerView: RecyclerView
    private val adapter by lazy {
        MoviesAdapter(mutableListOf()) {
            openDetailsActivity(it)
        }
    }
    private var linearLayoutManager = GridLayoutManager(this, 2)
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var txtNoConnection: TextView
    private lateinit var imageNoConnection: ImageView
    private var moviesPage = 1

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moviesViewModel.getPopularMovies(
            moviesPage
        )
        moviesViewModel.movies.observe(this, {
            recyclerView.visibility = View.VISIBLE
            adapter.appendMovies(it)
            deleteNoConnectionSign()
            refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(this, {
            // повторить попытку подключения через 10сек
            Handler(Looper.getMainLooper()).postDelayed({
                moviesViewModel.getPopularMovies(
                    moviesPage
                )
            }, 10000)
            onError(it)
            refreshLayout.isRefreshing = false
        })

        initFileds()
    }

    private fun initFileds() {
        initRecycler()
        initBottomNav()
        refreshLayout = findViewById(R.id.refresh_layout)
        txtNoConnection = findViewById(R.id.txt_no_connection)
        imageNoConnection = findViewById(R.id.image_no_connection)

        deleteNoConnectionSign()

        refreshLayout.setOnRefreshListener {
            moviesViewModel.getPopularMovies(
                moviesPage
            )
            refreshLayout.isRefreshing = false
        }

        refreshLayout.isEnabled = true
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = linearLayoutManager

        recyclerView.adapter = adapter

        attachPopularMoviesOnScrollListener()
    }

    private fun getTopRatedMovies() {
        moviesViewModel.getTopRatedMovies(
            1
        )
        attachPopularMoviesOnScrollListener()
    }

    private fun onError(text: String) {
        txtNoConnection.text = text
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
                    moviesPage += 1
                    moviesViewModel.getPopularMovies(
                        moviesPage
                    )
                }
            }
        })
    }

    private fun initBottomNav() {
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    if (!isAddedFragment) {
                        recyclerView.scrollToPosition(0)
                        moviesViewModel.getPopularMovies(
                            1
                        )
                    }
                    changeFocusOnBottomNav(R.id.page_1)
                    backToHomeScreen()
                    true
                }
                R.id.page_2 -> {
                    openFavoriteListFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun changeFocusOnBottomNav(id: Int) {
        val menuItem: MenuItem = bottomNav.menu.findItem(id)
        menuItem.isChecked = true
    }

    private fun backToHomeScreen() {
        isAddedFragment = false

        supportFragmentManager
            .popBackStack()

        refreshLayout.isEnabled = true
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true

        replaceFragment(
            DetailsFragment.newInstance(movie),
            R.id.container
        )

        refreshLayout.isEnabled = false
    }

    private fun openSeriesDetailsActivity(series: Series) {
        isAddedFragment = true

        replaceFragment(
            SeriesDetailsFragment.newInstance(series, MAIN_ACTIVITY),
            R.id.container
        )

        refreshLayout.isEnabled = false
    }

//    private fun openSeriesFragment() {
//        isAddedFragment = true
//
//        val fragment = SeriesFragment.newInstance()
//
//        replaceFragment(
//            fragment,
//            R.id.relative_container
//        )
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.d("onOptionsItemSelected", "onOptionsItemSelected")
//        val id = item.itemId
//        if(id == R.id.top_rated) {
//            Log.d("onOptionsItemSelected", "top rated")
//
//        } else{
//            Log.d("onOptionsItemSelected", "popular")
//
//        }
//
//        return true
//    }

    private fun openFavoriteListFragment() {
        isAddedFragment = true

        val fragment = FavoriteListFragment.newInstance()

        replaceFragment(
            fragment,
            R.id.relative_container
        )
    }

    private fun replaceFragment(
        fragment: Fragment,
        container: Int
    ) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(container, fragment, fragment.tag)
            .addToBackStack(null)
            .commit()
    }

    override fun onClick(movie: Movie) {
        openDetailsActivity(movie)
    }

    override fun onClick(series: Series) {
        openSeriesDetailsActivity(series)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            isAddedFragment = false

            supportFragmentManager
                .popBackStackImmediate()

            refreshLayout.isEnabled = true
        } else {
            showExitDialog()
        }
    }

    private fun showExitDialog() {
        val builder = MaterialAlertDialogBuilder(this)

        builder.setTitle(R.string.exit_text)
            .setPositiveButton(getString(R.string.confirm_exit)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.cancel_exit)) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

}

