@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.payment_status_crossfade
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.args.PaymentResultArgs
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentConnectionLostContent
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentSuccessContent
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentUnknownErrorContent
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
            PaymentResultArgs(transactionId, submitTransactionResultStatus, receiverName, amount)
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
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun PaymentResultScreenContent(
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
        Crossfade(
            targetState = state.paymentStatus,
            animationSpec = tween(durationMillis = 300),
            label = stringResource(Res.string.payment_status_crossfade)
        ) { currentPaymentStatus ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (currentPaymentStatus) {
                    SubmissionStatus.CONNECTION_LOST -> {
                        PaymentConnectionLostContent(state, interactionListener)
                    }

                    SubmissionStatus.UNKNOWN_ERROR -> {
                        PaymentUnknownErrorContent(state, interactionListener)
                    }

                    SubmissionStatus.SUCCESS -> {
                        PaymentSuccessContent(state, interactionListener)
                    }
                }
            }
        }
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

        is PaymentResultEffect.NavigateToPrePaymentScreen -> onCancelClicked()
    }
}