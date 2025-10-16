package net.thechance.mena.wallet.presentation.screen.confirm_payment

import net.thechance.mena.wallet.presentation.base.ErrorState

data class ConfirmPaymentScreenState(
    val isGetUserLoading: Boolean = false,
    val isGetBalanceLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val paymentUiState: PaymentUiState = PaymentUiState(),
    val receiverUiState: ReceiverUiState = ReceiverUiState(),
    val isPayButtonLoading: Boolean = false
){
    val isLoading: Boolean
        get() = isGetUserLoading || isGetBalanceLoading
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