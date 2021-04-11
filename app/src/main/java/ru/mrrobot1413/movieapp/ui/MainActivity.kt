package ru.mrrobot1413.movieapp.ui

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.movieapp.R
import ru.mrrobot1413.movieapp.interfaces.MovieClickListener
import ru.mrrobot1413.movieapp.model.Movie

class MainActivity : AppCompatActivity(), MovieClickListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

    companion object {
        const val MOVIE = "movie"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom)
        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun openDetailsFragment(id: Int, holder: RelativeLayout, source: Int) {
        val bundle = Bundle()
        bundle.putInt(MOVIE, id)
        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.fragment_open_enter)
            .setExitAnim(R.anim.fragment_close_exit).setPopEnterAnim(R.anim.nav_default_pop_enter_anim).setPopExitAnim(R.anim.nav_default_pop_exit_anim).build()
        navController.navigate(R.id.detailsFragment, bundle, navOptions, null)
    }

    override fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    override fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.homeFragment) {
            val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)

            builder.setTitle(R.string.exit_text)
                .setPositiveButton(getString(R.string.confirm_exit)) { _, _ ->
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel_exit)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.show()
        } else {
            super.onBackPressed()
        }
    }
}

