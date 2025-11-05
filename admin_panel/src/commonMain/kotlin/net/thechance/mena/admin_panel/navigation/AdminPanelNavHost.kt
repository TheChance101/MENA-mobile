package net.thechance.mena.admin_panel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.admin_panel.presentation.screen.deposit.DepositScreen
import net.thechance.mena.admin_panel.presentation.screen.dukan_managements.DukanManagementsScreen
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsScreen
import net.thechance.mena.admin_panel.presentation.screen.login.LoginScreen
import net.thechance.mena.admin_panel.presentation.screen.user_managements.UserManagementScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("Admin NavController not provided")
}

@Composable
fun AdminPanelNavHost(
    navController: NavHostController = rememberNavController(),
    isUserLoggedIn: Boolean
) {

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) UsersManagement else Login
    ) {
        composable<Login> {
            LoginScreen()
        }
        composable<UsersManagement> {
            UserManagementScreen()
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
    }
}