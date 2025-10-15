@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentStatusBody
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun PaymentResultScreen(
    transactionId: String,
    submitTransactionResultStatus: String,
    receiverName: String,
    amount: Double,
    onNavigateBackClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    onNavigateToTransactionDetailsClicked: (String) -> Unit,
    viewModel: PaymentResultViewModel = koinViewModel(parameters = {
        parametersOf(
            PaymentResultArgs(transactionId, submitTransactionResultStatus)
        )
    })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onPaymentResultEffect(
                effect,
                onNavigateBackClicked = onNavigateBackClicked,
                onCancelClicked = onCancelClicked,
                onNavigateToTransactionDetailsClicked = { transactionId ->
                    onNavigateToTransactionDetailsClicked(transactionId)
                }
            )
        }
    )
    PaymentResultScreenContent(
        receiverName = receiverName,
        amount = amount, state = state,
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
            paymentStatus = state.paymentStatus,
            interactionListener = interactionListener
        )
    }
}

private fun onPaymentResultEffect(
    effect: PaymentResultEffect,
    onNavigateBackClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    onNavigateToTransactionDetailsClicked: (String) -> Unit
) {
    when (effect) {
        is PaymentResultEffect.NavigateBack -> onNavigateBackClicked()
        is PaymentResultEffect.NavigateToTransactionDetails -> onNavigateToTransactionDetailsClicked(
            effect.transactionId.toString()
        )
        is PaymentResultEffect.NavigateToScreenBeforePaymentProcess -> onCancelClicked
    }
}