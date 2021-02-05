package ru.mrrobot1413.lesson3homework.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson3homework.R
import ru.mrrobot1413.lesson3homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson3homework.data.DataStorage
import ru.mrrobot1413.lesson3homework.model.Movie
import ru.mrrobot1413.lesson3homework.ui.fragments.DetailsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val moviesList = DataStorage.moviesList
    private lateinit var adapter: MoviesAdapter
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false

    companion object {
        const val MOVIE = "MOVIE"
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
                R.id.page_2 -> {
                    openFavoriteActivity()

                    true
                }
                else -> false
            }
        }
        updateNavigationBarState()
        selectBottomNavigationBarItem(bottomNav.selectedItemId)
    }

    private fun updateNavigationBarState() {
        val actionId: Int = bottomNav.selectedItemId
        selectBottomNavigationBarItem(actionId)
    }

    private fun selectBottomNavigationBarItem(itemId: Int) {
        val item: MenuItem = bottomNav.menu.findItem(itemId)
        item.isChecked = true
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.relative, DetailsFragment.newInstance(movie), "DetailsFragment")
            .addToBackStack("null")
            .commit()
    }

    private fun openFavoriteActivity() {
        val intent = Intent(this, FavoriteActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
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

