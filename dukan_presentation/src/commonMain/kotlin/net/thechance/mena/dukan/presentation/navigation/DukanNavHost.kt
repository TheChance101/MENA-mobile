package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun DukanNavHost() {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = MainScreenRoute,
        ) {
            composable<MainScreenRoute> {
                // Main Screen
            }

            composable <PendingDukanScreen> {
                // pending dukan screen
            }

            // Other Routs
        }
    }
}
