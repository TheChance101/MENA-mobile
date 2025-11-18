@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.confirm_payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.confirm_payment_header
import mena.wallet_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.BackIcon
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.PaymentResultScreenRoute
import net.thechance.mena.wallet.presentation.screen.confirm_payment.component.PayButton
import net.thechance.mena.wallet.presentation.screen.confirm_payment.component.PaymentDetailsSection
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ConfirmPaymentScreen(
    navigateBack: () -> Unit,
    viewModel: ConfirmPaymentViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onConfirmPaymentEffect(
                effect = effect,
                navController = navController,
                navigateBack = navigateBack
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = { BackIcon() },
                onLeadingClick = interactionListener::onBackButtonClicked,
            )
        },
        isLoading = state.isLoading,
        errorState = state.errorState,
        onRetry = { interactionListener.onRefresh() }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            PaymentDetailsSection(
                modifier = Modifier.fillMaxWidth().weight(1f),
                userMessage = state.userMessage,
                payment = state.paymentUiState,
                receiver = state.receiverUiState
            )

            PayButton(
                isLoading = state.isPayButtonLoading,
                isEnabled = state.paymentUiState.status,
                onClick = interactionListener::onPayButtonClicked,
                payAmount = state.paymentUiState.amount
            )
        }
    }
}

private fun onConfirmPaymentEffect(
    effect: ConfirmPaymentEffect,
    navController: NavController,
    navigateBack: () -> Unit,
) {
    when (effect) {
        ConfirmPaymentEffect.NavigateBack -> {
            navigateBack()
        }

        is ConfirmPaymentEffect.NavigateToPaymentResultScreen -> {
            navController.navigate(
                PaymentResultScreenRoute(
                    transactionId = effect.transactionId.toString(),
                    submitTransactionResultStatus = effect.submissionStatus.name,
                    amount = effect.amount,
                    receiverName = effect.receiverName
                )
            )
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