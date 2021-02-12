package ru.mrrobot1413.lesson8homework.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.animation.Animation
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson8homework.R
import ru.mrrobot1413.lesson8homework.adapters.MoviesAdapter
import ru.mrrobot1413.lesson8homework.data.DataStorage
import ru.mrrobot1413.lesson8homework.interfaces.FragmentsClickListener
import ru.mrrobot1413.lesson8homework.model.Movie
import ru.mrrobot1413.lesson8homework.ui.fragments.DetailsFragment
import ru.mrrobot1413.lesson8homework.ui.fragments.FavoriteListFragment


class MainActivity : AppCompatActivity(), FragmentsClickListener {

    private lateinit var recyclerView: RecyclerView
    private val moviesList = DataStorage.moviesList
    private lateinit var adapter: MoviesAdapter
    private lateinit var bottomNav: BottomNavigationView
    private var isAddedFragment: Boolean = false
    private val beginTransaction = supportFragmentManager.beginTransaction()

    companion object {
        const val DETAILS_FRAGMENT = "DetailsFragment"
        const val FAVORITE_LIST_FRAGMENT = "FavoriteListFragment"
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

        adapter = MoviesAdapter(moviesList) { movie ->
            openDetailsActivity(movie)
        }
        recyclerView.adapter = adapter

    }

    private fun initBottomNav() {
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val menuItem: MenuItem = bottomNav.menu.findItem(R.id.page_1)
                    menuItem.isChecked = true

                    openMainActivity()

                    adapter.notifyDataSetChanged()
                    true
                }
                R.id.page_2 -> {
                    if (!isAddedFragment) {
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

        replaceFragment(
            DetailsFragment.newInstance(movie),
            R.id.relative,
            false,
            null,
            null
        )
    }

    private fun openFavoriteActivity() {
        isAddedFragment = true

        replaceFragment(
            FavoriteListFragment.newInstance(),
            R.id.container,
            true,
            R.anim.anim_enter_favorite_list,
            R.anim.anim_exit_favorite_list
        )
    }

    private fun replaceFragment(
        fragment: Fragment,
        container: Int,
        addAnimation: Boolean,
        @AnimatorRes @AnimRes animEnter: Int?,
        @AnimatorRes @AnimRes animExit: Int?
    ) {
        val supportFragmentManager =
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(container, fragment, FAVORITE_LIST_FRAGMENT)
        if (addAnimation) {
            supportFragmentManager
                .setCustomAnimations(animEnter!!, animExit!!)
        } else {
            supportFragmentManager
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
        supportFragmentManager.commit()
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

