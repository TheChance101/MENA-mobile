package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.connection_lost_try_again
import mena.wallet_presentation.generated.resources.failed_dark
import mena.wallet_presentation.generated.resources.transaction_failed
import mena.wallet_presentation.generated.resources.try_again
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.navigation.LocalDarkTheme
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultInteractionListener
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PaymentConnectionLostContent(
    state: PaymentResultScreenState,
    interactionListener: PaymentResultInteractionListener
) {
    val failedTransactionIcon =
        if (LocalDarkTheme.current) painterResource(Res.drawable.failed_dark)
        else
            painterResource(Res.drawable.transaction_failed)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.weight(1f))
        PaymentResultCard(
            image = failedTransactionIcon,
            title = stringResource(Res.string.transaction_failed),
            description = stringResource(Res.string.connection_lost_try_again),
            paymentStatus = state.paymentStatus
            )

        Spacer(modifier = Modifier.weight(1f))
        PaymentActionButtons(
            modifier = Modifier.padding(top = 12.dp),
            primaryButtonText = stringResource(Res.string.try_again),
            onPrimaryButtonClick = interactionListener::onTryAgainClicked,
            onCancelClicked = interactionListener::onCloseClicked,
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
        PaymentConnectionLostContent(
            interactionListener = object : PaymentResultInteractionListener {
                override fun onBackClicked() {}
                override fun onTryAgainClicked() {}
                override fun onCloseClicked() {}
                override fun onShowTransactionDetailsClicked() {}
            },
            state = PaymentResultScreenState(
                paymentStatus = SubmissionStatus.CONNECTION_LOST,
                receiverName = "Menna",
                amount = 100.00,
                isLoading = false,
                isCloseButtonEnabled = true,
                isTryAgainButtonEnabled = true
            )
        )
    }
}
