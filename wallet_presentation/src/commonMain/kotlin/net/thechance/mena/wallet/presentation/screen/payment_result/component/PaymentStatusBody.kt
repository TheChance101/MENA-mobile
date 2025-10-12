package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.connection_lost_try_again
import mena.wallet_presentation.generated.resources.show_transaction_details
import mena.wallet_presentation.generated.resources.transaction_failed
import mena.wallet_presentation.generated.resources.transaction_success
import mena.wallet_presentation.generated.resources.transaction_successful
import mena.wallet_presentation.generated.resources.try_again
import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultInteractionListener
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PaymentStatusBody(
    interactionListener: PaymentResultInteractionListener,
    paymentStatus: SubmitTransactionResultStatus = SubmitTransactionResultStatus.FAILURE,
    description: String = stringResource(Res.string.connection_lost_try_again),
    receiverName: String = "",
    amount: Double = 0.0
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (paymentStatus == SubmitTransactionResultStatus.FAILURE) {
            PaymentResultCard(
                image = painterResource(Res.drawable.transaction_failed),
                title = stringResource(Res.string.transaction_failed),
                description = description,
                paymentStatus = paymentStatus,
                modifier = Modifier.align(Alignment.Center)
            )
            PaymentStatusButtons(
                primaryButtonText = stringResource(Res.string.try_again),
                onPrimaryButtonClick = interactionListener::onTryAgainClicked,
                onCancelClicked = interactionListener::onCancelClicked,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else {
            PaymentResultCard(
                image = painterResource(Res.drawable.transaction_success),
                title = stringResource(Res.string.transaction_successful),
                name = receiverName,
                amount = amount,
                paymentStatus = paymentStatus,
                modifier = Modifier.align(Alignment.Center)
            )
            PaymentStatusButtons(
                primaryButtonText = stringResource(Res.string.show_transaction_details),
                onPrimaryButtonClick = interactionListener::onShowTransactionDetailsClicked,
                onCancelClicked = interactionListener::onCancelClicked,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}