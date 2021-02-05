package ru.mrrobot1413.lesson3homework.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson3homework.R
import ru.mrrobot1413.lesson3homework.adapters.FavoriteAdapter
import ru.mrrobot1413.lesson3homework.data.DataStorage
import ru.mrrobot1413.lesson3homework.model.Movie
import ru.mrrobot1413.lesson3homework.ui.fragments.DetailsFragment

class FavoriteActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_favorite)

        val noMoviesSign: TextView = this.findViewById(R.id.txt_no_movie)
        noMoviesSign.visibility = View.VISIBLE
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FavoriteAdapter(DataStorage.favoriteList, noMoviesSign){ movie ->
            openDetailsActivity(movie)
        }

        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    openMainActivity()
                    true
                }
                else -> false
            }
        }

        val item: MenuItem = bottomNav.menu.findItem(R.id.page_2)
        item.isChecked = true
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.relative, DetailsFragment.newInstance(movie), "DetailsFragment")
            .addToBackStack("null")
            .commit()
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
            .setPositiveButton(getString(R.string.confirm_exit)) { dialog, which ->
                finish()
            }
            .setNegativeButton(getString(R.string.cancel_exit)) { dialog, which ->
                dialog.dismiss()
            }
        builder.show()
    }
}