@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.navigation.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.thechance.mena.wallet.presentation.screen.confirm_payment.ConfirmPaymentScreen
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class ConfirmPaymentScreenRoute(
    val transactionId: String,
    val amount: Double
) : WalletRoute()

fun NavGraphBuilder.confirmPaymentScreenRoute(navController: NavController){
    composable<ConfirmPaymentScreenRoute> {
        ConfirmPaymentScreen()
    }
}