package ru.mrrobot1413.movieapp.ui

import androidx.navigation.fragment.NavHostFragment

class NavHostFragment : NavHostFragment() {
    override fun createFragmentNavigator() =
        FragmentNavigator(requireContext(), childFragmentManager, id)
}