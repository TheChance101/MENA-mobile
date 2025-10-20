package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreen

@Serializable
data class PaymentResultScreenRoute(
    val transactionId: String,
    val submitTransactionResultStatus: String,
    val receiverName: String,
    val amount: Double
) : WalletRoute()

fun NavGraphBuilder.paymentResultScreenRoute(navController: NavController) {
    composable<PaymentResultScreenRoute> { backStackEntry ->
        PaymentResultScreen(
            onNavigateBackClicked = { navController.popBackStack() },
            onNavigateToTransactionDetailsClicked = { receiverId ->
                navController.navigate(TransactionDetailsScreenRoute(receiverId))
            },
            onCancelClicked = {
                navController.navigate(WalletMainScreenRoute) {
                    popUpTo(WalletMainScreenRoute) { inclusive = true }
                }
            }
        )
    }
}