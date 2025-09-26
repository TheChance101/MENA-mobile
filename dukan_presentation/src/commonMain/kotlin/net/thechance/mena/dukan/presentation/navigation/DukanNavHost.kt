package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import net.thechance.mena.dukan.presentation.screen.createDukan.CreateDukanScreen
import net.thechance.mena.dukan.presentation.screen.main.MainScreen
import net.thechance.mena.dukan.presentation.screen.pendingDukan.PendingDukanScreen
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import org.koin.compose.koinInject

@Composable
fun DukanNavHost(
    dukanNavigator: DukanNavigator = koinInject(),
) {

    val navController = rememberNavController()

    ObserveAsEffect(effects = dukanNavigator.dukanEffects) { effect ->
        when (effect) {
            is DukanEffect.Navigate -> navController.navigate(
                route = effect.route,
                navOptions = effect.navOptions
            )
            is DukanEffect.NavigateUp -> navController.navigateUp()
            is DukanEffect.PopBackStackWithArgs -> navController.popBackStackWithArgs(effect.arguments)
        }
    }

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = dukanNavigator.startDestination,
        ) {
            composable<DukanRoute.MainScreenRoute> {
                MainScreen()
            }

            composable<DukanRoute.CreateDukanScreenRoute> {
                CreateDukanScreen()
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
        }
    }
}

private fun NavController.popBackStackWithArgs(args: Map<String, Any>) {
    val backStack = previousBackStackEntry ?: return
    args.forEach { (key, value) -> backStack.savedStateHandle[key] = value }
    popBackStack()
}