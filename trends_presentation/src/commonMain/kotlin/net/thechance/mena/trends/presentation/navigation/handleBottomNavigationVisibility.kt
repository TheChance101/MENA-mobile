package net.thechance.mena.trends.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
@Suppress("ComposableNaming")
internal fun handleBottomNavigationVisibility(
    updateBottomNavigationVisibility: (Boolean) -> Unit
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(currentDestination) {
        val shouldShowBottomNav = isBottomNavigationVisible(currentDestination)
        updateBottomNavigationVisibility(shouldShowBottomNav)
    }
}

private fun isBottomNavigationVisible(destination: NavDestination?): Boolean {
    val routeWithBottomNavigation = listOf(
        Route.MainContainer::class,
        Route.Home::class,
        Route.Categories::class,
    )

    return destination?.hierarchy?.any { destination ->
        routeWithBottomNavigation.any { route ->
            destination.hasRoute(route)
        }
    } ?: true
}