package ru.mrrobot1413.movieapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("fragment")
class FragmentNavigator(
    context: Context,
    fm: FragmentManager,
    containerId: Int
) : FragmentNavigator(context, fm, containerId) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val shouldSkip = navOptions?.run {
            popUpTo == destination.id && !isPopUpToInclusive
        }

        return if (shouldSkip == true) null
        else super.navigate(destination, args, navOptions, navigatorExtras)
    }
}