package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.dukan.presentation.screen.CategoriesScreen
import net.thechance.mena.dukan.presentation.screen.CreateDukanScreen
import net.thechance.mena.dukan.presentation.screen.MainScreen
import net.thechance.mena.dukan.presentation.screen.MyDukanScreen
import net.thechance.mena.dukan.presentation.screen.RequestPendingScreen

@Composable
fun DukanNavHost() {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = DukanRoute.MainScreenRoute,
        ) {
            composable<DukanRoute.MainScreenRoute> {
                MainScreen(
                    navigateToCategories = { navController.navigate(DukanRoute.CategoryScreenRoute) },
                    navigateToCreateDukan = { navController.navigate(DukanRoute.CreateDukanRoute) }
                )
            }
            composable<DukanRoute.CategoryScreenRoute> {
                CategoriesScreen(
                    navigateBack = navController::popBackStack
                )
            }
            composable<DukanRoute.CreateDukanRoute> {
                CreateDukanScreen(
                    navigateBack = navController::popBackStack,
                    navigateToRequestPending = { navController.navigate(DukanRoute.RequestPendingScreenRoute) }
                )
            }
            composable<DukanRoute.MyDukanScreenRoute> {
                MyDukanScreen()
            }
            composable<DukanRoute.RequestPendingScreenRoute> {
                RequestPendingScreen()
            }
        }
    }
}
