package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.wallet.WalletMainScreen
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data object WalletMainScreenRoute : WalletRoute()

@OptIn(ExperimentalUuidApi::class)
fun NavGraphBuilder.walletMainScreenRoute(navController: NavController, navigateBack: () -> Unit) {
    composable<WalletMainScreenRoute> {
        WalletMainScreen(
            onNavigateBackClicked = navigateBack,
            navigateToTransactionHistory = { navController.navigate(TransactionsHistoryScreenRoute) },
            navigateToStatementsHistory = { navController.navigate(StatementsHistoryScreenRoute) },
            navigateToPaymentScreen = { amount, transactionId ->
                navController.navigate(
                    ConfirmPaymentScreenRoute(
                        amount = amount,
                        transactionId = transactionId.toString()
                    )
                )
            }
        )
    }
}