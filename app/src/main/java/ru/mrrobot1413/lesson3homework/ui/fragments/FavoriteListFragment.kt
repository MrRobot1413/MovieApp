package ru.mrrobot1413.lesson3homework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.mrrobot1413.lesson3homework.R
import ru.mrrobot1413.lesson3homework.adapters.FavoriteAdapter
import ru.mrrobot1413.lesson3homework.data.DataStorage
import ru.mrrobot1413.lesson3homework.model.Movie

class FavoriteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var noMoviesSign: TextView
    private var isAddedFragment: Boolean = false

    companion object {
        fun newInstance(): DetailsFragment {
            val args = Bundle()

            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initFields(view)
        initBottomNav()
        val item: MenuItem = bottomNav.menu.findItem(R.id.page_2)
        item.isChecked = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecycler()
    }

    private fun initFields(view: View){
        noMoviesSign = view.findViewById(R.id.txt_no_movie)
        noMoviesSign.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.recycler_view)
        bottomNav = view.findViewById(R.id.bottom_navigation)
    }

    private fun initRecycler(){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = FavoriteAdapter(DataStorage.favoriteList, noMoviesSign){ movie ->
            openDetailsActivity(movie)
        }
    }

    private fun initBottomNav(){
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    openMainActivity()

                    val itemMenu: MenuItem = bottomNav.menu.findItem(R.id.page_1)
                    itemMenu.isChecked = true
                    true
                }
                else -> false
            }
        }
    }

    private fun openMainActivity() {
        isAddedFragment = false
        activity?.supportFragmentManager
            ?.popBackStack()
    }

    private fun openDetailsActivity(movie: Movie) {
        isAddedFragment = true
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.relative, DetailsFragment.newInstance(movie), "DetailsFragment")
            ?.addToBackStack(null)
            ?.commit()
    }
}