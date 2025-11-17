package net.thechance.mena.admin_panel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreen
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementsScreen
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsScreen
import net.thechance.mena.admin_panel.presentation.screen.login.LoginScreen
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsScreen
import net.thechance.mena.admin_panel.presentation.screen.splash_screen.SplashScreen
import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("Admin NavController not provided")
}

@Composable
fun AdminPanelNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Splash,
        ) {
            composable<Splash> {
                SplashScreen()
            }
            composable<Login> {
                LoginScreen()
            }
            composable<UsersManagement> {
                UsersManagementScreen()
            }
            composable<Deposit> {
                DepositScreen()
            }
            composable<DukanRequests> {
                DukanRequestsScreen()
            }
            composable<DukanManagement> {
                DukanManagementsScreen()
            }
            composable<DukanDetails> {
                DukanDetailsScreen()
            }
        }
    }
}
