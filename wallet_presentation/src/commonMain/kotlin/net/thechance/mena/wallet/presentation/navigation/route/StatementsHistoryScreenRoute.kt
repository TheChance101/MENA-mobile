package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementHistoryScreen

@Serializable
data object StatementsHistoryScreenRoute : WalletRoute()

fun NavGraphBuilder.statementsHistoryScreenRoute(navController: NavController) {
    composable<StatementsHistoryScreenRoute> {
        StatementHistoryScreen()
    }
}