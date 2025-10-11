package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.dukan.presentation.screen.manageDukan.ManageDukanScreen
import net.thechance.mena.dukan.presentation.screen.createDukan.CreateDukanScreen
import net.thechance.mena.dukan.presentation.screen.createProduct.CreateProductScreen
import net.thechance.mena.dukan.presentation.screen.createShelf.CreateShelfScreen
import net.thechance.mena.dukan.presentation.screen.dukanDetails.DukanDetailsScreen
import net.thechance.mena.dukan.presentation.screen.main.MainScreen
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfScreen
import net.thechance.mena.dukan.presentation.screen.pendingDukan.PendingDukanScreen
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsScreen

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
                MainScreen()
            }

            composable<DukanRoute.CreateDukanScreenRoute> {
                CreateDukanScreen()
            }

            composable<DukanRoute.CreateShelfScreenRoute> {
                CreateShelfScreen()
            }

            composable<DukanRoute.ManageDukanScreenRoute> {
                ManageDukanScreen()
            }

            composable<DukanRoute.MyDukanScreenRoute> {
                // MyDukanScreen()
            }
            composable<DukanRoute.PendingScreenRoute> { backStackEntry ->
                val route: DukanRoute.PendingScreenRoute =
                    backStackEntry.toRoute()
                PendingDukanScreen(
                    dukanName = route.dukanName,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<DukanRoute.ManageShelfScreenRoute> {
                ManageShelfScreen()
            }
            composable<DukanRoute.CreateProductScreenRoute> {
                CreateProductScreen()
            }
            composable<DukanRoute.DukanDetails> { backStackEntry ->
                val route: DukanRoute.DukanDetails = backStackEntry.toRoute()
                DukanDetailsScreen(route.dukanId)
            }
            composable<DukanRoute.ShelfDetails> { backStackEntry ->
                val route: DukanRoute.ShelfDetails = backStackEntry.toRoute()
                ShelfDetailsScreen(route.shelfId, route.shelfName)
            }
        }
    }
}