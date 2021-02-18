package ru.mrrobot1413.lesson8homework.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.interfaces.MovieClickListener
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.ui.fragments.DetailsFragment
import ru.mrrobot1413.lesson8homework.ui.fragments.FavoriteListFragment
import ru.mrrobot1413.lesson8homework.viewModels.FavoriteListViewModel
import ru.mrrobot1413.lesson8homework.viewModels.MoviesViewModel

class MainActivity : AppCompatActivity(), MovieClickListener {

    private lateinit var recyclerView: RecyclerView
    private val moviesViewModel by lazy {
        ViewModelProvider(this).get(MoviesViewModel::class.java)
    }
    private val adapter by lazy {
        MoviesAdapter { movie ->
            openDetailsActivity(movie)
        }
    }
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false

    companion object{
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()
        initBottomNav()
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)

        moviesViewModel.movies.observe(this, {
            adapter.setMovies(it)
            adapter.notifyDataSetChanged()
        })

        recyclerView.adapter = adapter
    }

    private fun initBottomNav() {
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    changeFocusOnBottomNavToMainActivity()
                    backToHomeScreen()
                    adapter.notifyDataSetChanged()
                    true
                }
                R.id.page_2 -> {
                    if (!isAddedFragment) {
                        openFavoriteListFragment()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun changeFocusOnBottomNavToMainActivity(){
        val menuItem: MenuItem = bottomNav.menu.findItem(R.id.page_1)
        menuItem.isChecked = true
    }

    private fun backToHomeScreen() {
        isAddedFragment = false

        supportFragmentManager
            .popBackStack()
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true

        replaceFragment(
            DetailsFragment.newInstance(movie, MAIN_ACTIVITY),
            R.id.relative
        )
    }

    private fun openFavoriteListFragment() {
        isAddedFragment = true

        replaceFragment(
            FavoriteListFragment.newInstance(),
            R.id.container
        )
    }

     fun replaceFragment(
        fragment: Fragment,
        container: Int
    ) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(container, fragment, fragment.tag)
            .commit()
    }

    override fun onClick(movie: Movie) {
        openDetailsActivity(movie)
    }

    override fun onBackPressed() {
        if (isAddedFragment) {
            isAddedFragment = false

            supportFragmentManager
                .popBackStack()
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

