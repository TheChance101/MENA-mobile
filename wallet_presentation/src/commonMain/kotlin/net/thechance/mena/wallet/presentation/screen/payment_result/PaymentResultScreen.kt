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
import androidx.navigation.NavController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.payment_status_crossfade
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.component.BackIcon
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.TransactionDetailsScreenRoute
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentBlockedReceiverContent
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentConnectionLostContent
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentSuccessContent
import net.thechance.mena.wallet.presentation.screen.payment_result.component.PaymentUnknownErrorContent
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun PaymentResultScreen(
    navigateBack: () -> Unit,
    viewModel: PaymentResultViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onPaymentResultEffect(
                effect,
                navController = navController,
                navigateBack = navigateBack
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
                    leadingContent = { BackIcon() },
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
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                when (currentPaymentStatus) {
                    SubmissionStatus.CONNECTION_LOST -> {
                        PaymentConnectionLostContent(state, interactionListener)
                    }

                    SubmissionStatus.UNKNOWN_ERROR -> {
                        PaymentUnknownErrorContent(state, interactionListener)
                    }

                    SubmissionStatus.BLOCKED_RECEIVER -> {
                        PaymentBlockedReceiverContent(state, interactionListener)
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
    navController: NavController,
    navigateBack: () -> Unit
) {
    when (effect) {
        is PaymentResultEffect.NavigateBack -> navController.popBackStack()
        is PaymentResultEffect.NavigateToTransactionDetails -> {
            navController.navigate(TransactionDetailsScreenRoute(effect.transactionId.toString()))
        }
        is PaymentResultEffect.NavigateToPrePaymentScreen -> navigateBack()
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentResultScreenErrorPreview() {
    MenaTheme {
        PaymentResultScreenContent(
            state = PaymentResultScreenState(
                paymentStatus = SubmissionStatus.UNKNOWN_ERROR
            ),
            interactionListener = object : PaymentResultInteractionListener {
                override fun onBackClicked() {}
                override fun onTryAgainClicked() {}
                override fun onCloseClicked() {}
                override fun onShowTransactionDetailsClicked() {}
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentResultScreenSuccessPreview() {
    MenaTheme {
        PaymentResultScreenContent(
            state = PaymentResultScreenState(
                paymentStatus = SubmissionStatus.SUCCESS,
                receiverName = "Ahmed Ali",
                amount = 31.99
            ),
            interactionListener = object : PaymentResultInteractionListener {
                override fun onBackClicked() {}
                override fun onTryAgainClicked() {}
                override fun onCloseClicked() {}
                override fun onShowTransactionDetailsClicked() {}
            }
        )
    }
}