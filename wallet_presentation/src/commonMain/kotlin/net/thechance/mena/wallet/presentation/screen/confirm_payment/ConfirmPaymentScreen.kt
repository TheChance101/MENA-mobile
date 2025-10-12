package net.thechance.mena.wallet.presentation.screen.confirm_payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.confirm_payment_header
import mena.wallet_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.presentation.component.ErrorView
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.confirm_payment.component.PayButton
import net.thechance.mena.wallet.presentation.screen.confirm_payment.component.PaymentDetailsSection
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ConfirmPaymentScreen(
    onNavigateBackClicked: () -> Unit,
    receiverId: String,
    amount: Double,
    navigateToPaymentResultScreen: (String, Double) -> Unit,
    viewModel: ConfirmPaymentViewModel = koinViewModel(
        parameters = { parametersOf(ConfirmPaymentArgs(receiverId, amount)) }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onConfirmPaymentEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                navigateToPaymentResultScreen = navigateToPaymentResultScreen
            )
        }
    )

    ConfirmPaymentScreenContent(state = state, interactionListener = viewModel)

}

@Composable
private fun ConfirmPaymentScreenContent(
    state: ConfirmPaymentScreenState,
    interactionListener: ConfirmPaymentInteractionListener,
) {
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.confirm_payment_header),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        tint = Theme.colorScheme.primary.primary,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onBackButtonClicked,
            )
        },
        errorState = state.errorState,
        onRetry = { interactionListener.onRefresh() }
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            state.errorState != null -> ErrorView(onRetry = { interactionListener.onRefresh() })

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Theme.spacing._16)
                        .padding(bottom = Theme.spacing._24)
                ) {
                    PaymentDetailsSection(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        paymentUiState = state.paymentUiState
                    )

                    PayButton(
                        isPayBtnLoading = state.isPayBtnLoading,
                        isEnabled = state.paymentUiState.status,
                        onClick = interactionListener::onPayButtonClicked,
                        payAmount = state.paymentUiState.amount
                    )
                }
            }
        }
    }

}

private fun onConfirmPaymentEffect(
    effect: ConfirmPaymentEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToPaymentResultScreen: (String, Double) -> Unit
) {
    when (effect) {
        ConfirmPaymentEffect.NavigateBack -> onNavigateBackClicked()
        is ConfirmPaymentEffect.NavigateToPaymentResultScreen -> {
            navigateToPaymentResultScreen(effect.receiverId, effect.amount)
        }
    }
}

@Preview
@Composable
private fun ConfirmPaymentScreenPreview() {
    MenaTheme {
        ConfirmPaymentScreenContent(
            state = ConfirmPaymentScreenState(),
            interactionListener = object : ConfirmPaymentInteractionListener {
                override fun onBackButtonClicked() {}
                override fun onPayButtonClicked() {}
                override fun onRefresh() {}
            }
        )
    }
}