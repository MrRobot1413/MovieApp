package ru.mrrobot1413.lesson8homework.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.model.MovieDetailed
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
    private lateinit var linearLayoutManager: GridLayoutManager
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var txtNoConnection: TextView
    private lateinit var imageNoConnection: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private var moviesPage = 1

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moviesViewModel.movies.observe(this, {
            recyclerView.visibility = View.VISIBLE
            adapter.appendMovies(it)
            deleteNoConnectionSign()
            refreshLayout.isRefreshing = false
        })

        moviesViewModel.error.observe(this, {
            // повторить попытку подключения через 5 сек
            Handler(Looper.getMainLooper()).postDelayed({
                moviesViewModel.getPopularMovies(
                    moviesPage
                )
            }, 5000)
            onError(it)
            refreshLayout.isRefreshing = false
        })

        initFields()
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        moviesViewModel.getPopularMovies(
            moviesPage
        )
    }

    private fun initFields() {
        linearLayoutManager =
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(this, 3)
            } else {
                GridLayoutManager(this, 2)
            }

        initRecycler()
        initBottomNav()
        refreshLayout = findViewById(R.id.refresh_layout)
        txtNoConnection = findViewById(R.id.txt_no_connection)
        imageNoConnection = findViewById(R.id.image_no_connection)
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.app_bar_layout)

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
                    moviesPage++
                    moviesViewModel.getPopularMovies(moviesPage)
                    attachPopularMoviesOnScrollListener()
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
                            moviesPage
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

        appBarLayout.visibility = View.VISIBLE

        refreshLayout.isEnabled = true

    }

    private fun openDetailsActivity(movie: MovieDetailed) {
        isAddedFragment = true
        replaceFragment(
            DetailsFragment.newInstance(movie),
            R.id.container
        )
        refreshLayout.isEnabled = false
    }

    private fun openSeriesDetailsActivity(series: Series) {
        if (!isAddedFragment) {
            isAddedFragment = true
            replaceFragment(
                SeriesDetailsFragment.newInstance(series, MAIN_ACTIVITY),
                R.id.container
            )
            refreshLayout.isEnabled = false
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun showTopRatedMovies(){
        moviesViewModel.getTopRatedMovies(1)
    }

    private fun showPopularMovies(){
        moviesViewModel.getPopularMovies(1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        adapter.appendMoviesFromMenu(mutableListOf())
        when (item.itemId) {
            R.id.top_rated -> showTopRatedMovies()
            R.id.popular -> showPopularMovies()
        }
        return true
    }

    private fun openFavoriteListFragment() {
        isAddedFragment = true
        replaceFragment(
            FavoriteListFragment.newInstance(),
            R.id.relative_layout
        )
        appBarLayout.visibility = View.GONE
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
        refreshLayout.isRefreshing = false
    }

    override fun onClick(movie: MovieDetailed) {
        openDetailsActivity(movie)
    }

    override fun onClick(series: Series) {
        openSeriesDetailsActivity(series)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager
                .popBackStack()

            refreshLayout.isEnabled = true
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            isAddedFragment = false
            appBarLayout.visibility = View.VISIBLE
        } else if (supportFragmentManager.backStackEntryCount == 0) {
            appBarLayout.visibility = View.VISIBLE
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

