package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class PaymentResultScreenRoute(
    val transactionId: String,
    val submitTransactionResultStatus: String,
    val receiverName: String,
    val amount: Double
) : WalletRoute() {
    init {
        Uuid.parse(transactionId)
        SubmissionStatus.valueOf(submitTransactionResultStatus)
    }
}

fun NavGraphBuilder.paymentResultScreenRoute(navController: NavController){
    composable<PaymentResultScreenRoute> { backStackEntry ->
        PaymentResultScreen(
            transactionId = backStackEntry.toRoute<PaymentResultScreenRoute>().transactionId,
            submitTransactionResultStatus = backStackEntry.toRoute<PaymentResultScreenRoute>().submitTransactionResultStatus,
            receiverName = backStackEntry.toRoute<PaymentResultScreenRoute>().receiverName,
            amount = backStackEntry.toRoute<PaymentResultScreenRoute>().amount,
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