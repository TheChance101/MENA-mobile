@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.TransactionDetailsScreenRoute
import net.thechance.mena.wallet.presentation.navigation.WalletMainScreenRoute
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentStatusBody
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun PaymentResultScreen(viewModel: PaymentResultViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onPaymentResultEffect(effect, navController = navController)
        }
    )
    PaymentResultScreenContent(
        receiverName = state.receiverName,
        amount = state.amount,
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun PaymentResultScreenContent(
    receiverName: String,
    amount: Double,
    state: PaymentResultScreenState,
    interactionListener: PaymentResultInteractionListener
) {
    WalletScaffold(
        topBar = {
            if (state.paymentStatus != SubmissionStatus.SUCCESS) {
                AppBar(
                    title = "",
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back_button)
                        )
                    },
                    onLeadingClick = interactionListener::onBackClicked
                )
            }
        }
    ) {
        PaymentStatusBody(
            receiverName = receiverName,
            amount = amount,
            status = state,
            paymentStatus = state.paymentStatus,
            interactionListener = interactionListener
        )
    }
}

private fun onPaymentResultEffect(effect: PaymentResultEffect, navController: NavController) {
    when (effect) {
        is PaymentResultEffect.NavigateBack -> navController.popBackStack()
        is PaymentResultEffect.NavigateToTransactionDetails -> {
            navController.navigate(TransactionDetailsScreenRoute(effect.transactionId.toString()))
        }

        is PaymentResultEffect.NavigateToScreenBeforePaymentProcess -> {
            navController.navigate(WalletMainScreenRoute) {
                popUpTo(WalletMainScreenRoute) { inclusive = true }
            }
        }
    }
}