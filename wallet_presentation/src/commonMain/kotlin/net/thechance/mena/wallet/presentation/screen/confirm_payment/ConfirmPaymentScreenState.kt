package net.thechance.mena.wallet.presentation.screen.confirm_payment

import net.thechance.mena.wallet.presentation.base.ErrorState

data class ConfirmPaymentScreenState(
    val isGetTransactionDetailsLoading: Boolean = false,
    val isGetBalanceLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val paymentUiState: PaymentUiState = PaymentUiState(),
    val receiverUiState: ReceiverUiState = ReceiverUiState(),
    val userMessage: String = "",
    val isPayButtonLoading: Boolean = false,
    val amount: Double = 0.0
){
    val isLoading: Boolean
        get() = isGetTransactionDetailsLoading || isGetBalanceLoading
    data class PaymentUiState(
        val amount: String = "",
        val status: Boolean = false,
        val balance: String = ""
    )

    data class ReceiverUiState(
        val name: String = "",
        val profileImg: String? = null
    )
}