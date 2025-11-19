package net.thechance.mena.admin_panel.presentation.screen.mainContainer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.admin_panel.navigation.Deposit
import net.thechance.mena.admin_panel.navigation.DukanManagement
import net.thechance.mena.admin_panel.navigation.DukanRequests
import net.thechance.mena.admin_panel.navigation.LocalNavController
import net.thechance.mena.admin_panel.navigation.Login
import net.thechance.mena.admin_panel.navigation.UsersManagement
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.component.AdminPanelScaffold
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainContainerScreen(
    viewModel: MainContainerViewmodel = koinViewModel()
) {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {

        val state by viewModel.state.collectAsStateWithLifecycle()

        ObserveAsEffect(
            effect = viewModel.uiEffect,
            onEffect = { effect ->
                onMainContainerEffect(
                    navController = navController,
                    effect = effect
                )
            }
        )
        MainContainerContent(
            state = state,
            interactionListener = viewModel
        )
    }
}

@Composable
private fun MainContainerContent(
    state: MainContainerScreenState,
    interactionListener: MainContainerInteractionListener
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isLoginScreen = currentDestination?.hasRoute(Login::class) ?: true

    AdminPanelScaffold(
        state = state,
        interactionListener = interactionListener,
        isLoginScreen = isLoginScreen,
        navController = navController,
        snackBarState = state.snackBar
    )
}

private fun onMainContainerEffect(
    navController: NavController,
    effect: MainContainerEffect
) {

    when (effect) {
        is MainContainerEffect.NavigateToAdminPanelScreen -> {

            navController.navigate(DukanManagement) {
                popUpTo(DukanManagement) { inclusive = true }
            }
        }

        is MainContainerEffect.NavigateToLogInScreen -> {
            navController.navigate(Login) {
                popUpTo(DukanManagement) { inclusive = true }
            }
        }

        is MainContainerEffect.NavigateToUsersManagementScreen -> {
            navController.navigate(UsersManagement)
        }

        is MainContainerEffect.NavigateToDukanManagementScreen -> {
            navController.navigate(DukanManagement)
        }

        is MainContainerEffect.NavigateToDukanRequestsScreen -> {
            navController.navigate(DukanRequests)
        }

        is MainContainerEffect.NavigateToDepositScreen -> {
            navController.navigate(Deposit)
        }
    }
}
