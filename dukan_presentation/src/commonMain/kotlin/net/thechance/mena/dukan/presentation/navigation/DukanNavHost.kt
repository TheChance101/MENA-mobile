package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.dukan.presentation.screen.createDukan.CreateDukanScreen

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
                //  MainScreen()
            }

            composable<DukanRoute.CreateDukanScreenRoute> {
                 CreateDukanScreen()
            }
            composable<DukanRoute.MyDukanScreenRoute> {
                // MyDukanScreen()
            }
            composable<DukanRoute.RequestPendingScreenRoute> {
                //   RequestPendingScreen()
            }
        }
    }
}
