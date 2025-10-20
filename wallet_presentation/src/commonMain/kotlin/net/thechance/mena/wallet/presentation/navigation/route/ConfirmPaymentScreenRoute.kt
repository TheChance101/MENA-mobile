@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.confirm_payment.ConfirmPaymentScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ConfirmPaymentScreenRoute(
    val transactionId: String,
    val amount: Double
) : WalletRoute() {
    init {
        Uuid.parse(transactionId)
    }
}

fun NavGraphBuilder.confirmPaymentScreenRoute(navController: NavController){
    composable<ConfirmPaymentScreenRoute> { backStackEntry ->
        ConfirmPaymentScreen(
            onNavigateBackClicked = navController::popBackStack,
            transactionId = backStackEntry.toRoute<ConfirmPaymentScreenRoute>().transactionId,
            amount = backStackEntry.toRoute<ConfirmPaymentScreenRoute>().amount,
            navigateToPaymentResultScreen = { receiverName, amount, transactionId, submitTransactionResultStatus ->
                navController.navigate(
                    PaymentResultScreenRoute(
                        transactionId = transactionId.toString(),
                        submitTransactionResultStatus = submitTransactionResultStatus.name,
                        amount = amount,
                        receiverName = receiverName
                    )
                )
            }
        )
    }
}