package net.thechance.mena.wallet.presentation.screen.transactiondetails

import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState

data class TransactionDetailsScreenState(
    val transaction: UiState<Transaction> = UiState.Idle,
    val isShareReceiptLoading: Boolean = false,
    val snackBar: SnackBarState = SnackBarState()
){
    data class Transaction(
        val id: String = "",
        val amount: String = "",
        val date: String = "",
        val otherParty: String = "",
        val transactionType: TransactionType = TransactionType.PAY,
        val transactionStatus: TransactionStatus = TransactionStatus.FAILED
    )

    enum class TransactionType{
        PAY,
        SEND,
        RECEIVE
    }

    enum class TransactionStatus{
        FAILED,
        SUCCESS
    }
}