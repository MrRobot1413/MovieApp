package ru.mrrobot1413.lesson3homework.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson3homework.R
import ru.mrrobot1413.lesson3homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson3homework.data.DataStorage
import ru.mrrobot1413.lesson3homework.interfaces.FragmentsClickListener
import ru.mrrobot1413.lesson3homework.model.Movie
import ru.mrrobot1413.lesson3homework.ui.fragments.DetailsFragment
import ru.mrrobot1413.lesson3homework.ui.fragments.FavoriteListFragment


class MainActivity : AppCompatActivity(), FragmentsClickListener {

    private lateinit var recyclerView: RecyclerView
    private val moviesList = DataStorage.moviesList
    private lateinit var adapter: MoviesAdapter
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false

    companion object{
        const val DETAILS_FRAGMENT = "DetailsFragment"
        const val FAVORITE_LIST_FRAGMENT = "FavoriteListFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()
        initBottomNav()
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MoviesAdapter(moviesList) { movie ->
            openDetailsActivity(movie)
        }
        recyclerView.adapter = adapter

    }

    private fun initBottomNav(){
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val menuItem: MenuItem = bottomNav.menu.findItem(R.id.page_1)
                    menuItem.isChecked = true
                    openMainActivity()
                    true
                }
                R.id.page_2 -> {
                    if(!isAddedFragment){
                        openFavoriteActivity()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun openMainActivity() {
        isAddedFragment = false
        supportFragmentManager
            .popBackStack()
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.anim_in, R.anim.anim_out)
            .replace(R.id.container, DetailsFragment.newInstance(movie), DETAILS_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }

    private fun openFavoriteActivity() {
        isAddedFragment = true
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.anim_in, R.anim.anim_out)
            .replace(R.id.container, FavoriteListFragment.newInstance(), FAVORITE_LIST_FRAGMENT)
            .addToBackStack(null)
            .commit()
    }

    override fun onClick(movie: Movie) {
        openDetailsActivity(movie)
    }

    override fun onBackPressed() {
        if(isAddedFragment){
            supportFragmentManager
                .popBackStack()
            isAddedFragment = false
        } else{
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

