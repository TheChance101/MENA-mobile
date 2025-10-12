package net.thechance.mena.wallet.presentation.screen.payment_result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaymentResultScreen(
    onNavigateBackClicked: () -> Unit,
    viewModel: PaymentResultViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onPaymentResultEffect(
                effect,
                onNavigateBackClicked,
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

}


private fun onPaymentResultEffect(
    effect: PaymentResultEffect,
    onNavigateBackClicked: () -> Unit,
) {
    when (effect) {
        is PaymentResultEffect.NavigateBack -> onNavigateBackClicked()
    }
}