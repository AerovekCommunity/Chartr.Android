package org.aerovek.chartr.util

import androidx.lifecycle.Observer
import androidx.navigation.NavController

class NavigationObserver(private val navController: NavController) : Observer<NavigationEvent> {
    override fun onChanged(event: NavigationEvent) {
        event.navigate(navController)
    }
}