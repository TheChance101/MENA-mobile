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
import mena.wallet_presentation.generated.resources.show_transaction_details
import mena.wallet_presentation.generated.resources.success_dark
import mena.wallet_presentation.generated.resources.transaction_success
import mena.wallet_presentation.generated.resources.transaction_successful
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.navigation.LocalDarkTheme
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultInteractionListener
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PaymentSuccessContent(
    state: PaymentResultScreenState,
    interactionListener: PaymentResultInteractionListener
) {
    val successTransactionIcon =
        if (LocalDarkTheme.current) painterResource(Res.drawable.success_dark)
        else
            painterResource(Res.drawable.transaction_success)
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.weight(1f))
        PaymentResultCard(
            image = successTransactionIcon,
            title = stringResource(Res.string.transaction_successful),
            name = state.receiverName,
            amount = state.amount,
            paymentStatus = state.paymentStatus,
        )

        Spacer(modifier = Modifier.weight(1f))
        PaymentActionButtons(
            modifier = Modifier.padding(top = 12.dp),
            primaryButtonText = stringResource(Res.string.show_transaction_details),
            onPrimaryButtonClick = interactionListener::onShowTransactionDetailsClicked,
            onCancelClicked = interactionListener::onCloseClicked,
            isLoading = state.isLoading,
            isCloseEnabled = state.isCloseButtonEnabled
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PaymentResultUnknownErrorPreview() {
    MenaTheme {
        PaymentSuccessContent(
            interactionListener = object : PaymentResultInteractionListener {
                override fun onBackClicked() {}
                override fun onTryAgainClicked() {}
                override fun onCloseClicked() {}
                override fun onShowTransactionDetailsClicked() {}
            },
            state = PaymentResultScreenState(
                paymentStatus = SubmissionStatus.SUCCESS,
                receiverName = "Menna",
                amount = 100.00,
                isLoading = false,
                isCloseButtonEnabled = true,
                isTryAgainButtonEnabled = true
            )
        )
    }
}