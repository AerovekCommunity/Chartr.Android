package org.aerovek.chartr.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections

sealed class NavigationEvent {
    class Directions(val directions: NavDirections) : NavigationEvent()
    class Action(val destinationId: Int) : NavigationEvent()
    class Back(val destinationId: Int = -1, val inclusive: Boolean = false) : NavigationEvent()

    fun navigate(navController: NavController) {
        when (this) {
            is Directions -> navController.navigate(directions)
            is Action -> navController.navigate(destinationId)
            is Back -> if (destinationId == -1) {
                navController.popBackStack()
            } else {
                navController.popBackStack(destinationId, inclusive)
            }
        }
    }
}