package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreen

@Serializable
data class TransactionDetailsScreenRoute(
    val id: String
) : WalletRoute()

fun NavGraphBuilder.transactionDetailsScreenRoute(navController: NavController){
    composable<TransactionDetailsScreenRoute> {
        TransactionDetailsScreen(onNavigateBackClicked = { navController.popBackStack() })
    }
}