package net.thechance.mena.wallet.presentation.screen.payment_result

import net.thechance.mena.wallet.presentation.model.SubmissionStatus

data class PaymentResultScreenState(
    val paymentStatus: SubmissionStatus = SubmissionStatus.SUCCESS,
    val receiverName: String = "",
    val amount: Double = 0.0,
    val isLoading: Boolean = false,
    val tryAgainAttempts: Int = 0,
    val isTryAgainButtonEnabled: Boolean = true,
    val isCloseButtonEnabled: Boolean = true,
)