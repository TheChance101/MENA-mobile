package net.thechance.mena.wallet.presentation.screen.payment_result

import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus

data class PaymentResultScreenState(
    val paymentStatus: SubmitTransactionResultStatus = SubmitTransactionResultStatus.SUCCESS,
    val isLoading: Boolean = false,
)