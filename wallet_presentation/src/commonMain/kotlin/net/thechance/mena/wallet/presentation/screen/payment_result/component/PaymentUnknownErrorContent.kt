package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.payment_failed_description
import mena.wallet_presentation.generated.resources.transaction_failed
import mena.wallet_presentation.generated.resources.try_again
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultInteractionListener
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PaymentUnknownErrorContent(
    state: PaymentResultScreenState,
    interactionListener: PaymentResultInteractionListener
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PaymentResultCard(
            image = painterResource(Res.drawable.transaction_failed),
            title = stringResource(Res.string.transaction_failed),
            description = stringResource(Res.string.payment_failed_description),
            paymentStatus = state.paymentStatus,
            modifier = Modifier.align(Alignment.Center)
        )
        PaymentActionButtons(
            primaryButtonText = stringResource(Res.string.try_again),
            onPrimaryButtonClick = interactionListener::onTryAgainClicked,
            onCancelClicked = interactionListener::onCloseClicked,
            modifier = Modifier.align(Alignment.BottomCenter),
            isLoading = state.isLoading,
            isCloseEnabled = state.isCloseButtonEnabled,
            isTryAgainEnabled = state.isTryAgainButtonEnabled
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PaymentResultUnknownErrorPreview() {
    MenaTheme {
        PaymentUnknownErrorContent(
            interactionListener = object : PaymentResultInteractionListener {
                override fun onBackClicked() {}
                override fun onTryAgainClicked() {}
                override fun onCloseClicked() {}
                override fun onShowTransactionDetailsClicked() {}
            },
            state = PaymentResultScreenState(
                paymentStatus = SubmissionStatus.UNKNOWN_ERROR,
                receiverName = "Menna",
                amount = 100.00,
                isLoading = false,
                isCloseButtonEnabled = true,
                isTryAgainButtonEnabled = true
            )
        )
    }
}