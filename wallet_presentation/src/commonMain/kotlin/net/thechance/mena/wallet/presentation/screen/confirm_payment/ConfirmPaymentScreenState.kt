package net.thechance.mena.wallet.presentation.screen.confirm_payment

import net.thechance.mena.wallet.presentation.base.ErrorState

data class ConfirmPaymentScreenState(
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val paymentUiState: PaymentUiState = PaymentUiState(),
    val receiverUiState: ReceiverUiState = ReceiverUiState(),
    val isPayBtnLoading: Boolean = false
){
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