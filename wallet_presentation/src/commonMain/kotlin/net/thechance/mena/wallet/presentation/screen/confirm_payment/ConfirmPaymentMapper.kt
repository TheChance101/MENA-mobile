package net.thechance.mena.wallet.presentation.screen.confirm_payment

import androidx.compose.runtime.Composable
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.confirm_payment_content_failed
import mena.wallet_presentation.generated.resources.confirm_payment_content_success
import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import net.thechance.mena.wallet.presentation.utils.formatAmount
import org.jetbrains.compose.resources.stringResource

fun PaymentConfirmation.toUi(amount: Double) = ConfirmPaymentScreenState.PaymentUiState(
    amount = formatAmount(amount),
    receiverName = receiverName,
    receiverImage = receiverImg,
    status = status,
    balance = formatAmount(balance)
)

@Composable
fun GetUserMessage(paymentStatus: Boolean, balance: String) : String{
    return if (paymentStatus) {
        stringResource(
            Res.string.confirm_payment_content_success,
            balance
        )
    } else {
        stringResource(
            Res.string.confirm_payment_content_failed,
            balance
        )
    }
}