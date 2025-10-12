package net.thechance.mena.wallet.presentation.screen.payment_result

import net.thechance.mena.wallet.domain.model.PaymentStatus

data class PaymentResultScreenState(
    val paymentStatus: PaymentStatus = PaymentStatus.SUCCESS,
    val isLoading: Boolean = false,
    val error: String? = null,
    val receiverName: String = "",
    val hasAppBar: Boolean = true
)