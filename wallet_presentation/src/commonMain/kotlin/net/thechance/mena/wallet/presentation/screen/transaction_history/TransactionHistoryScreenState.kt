package net.thechance.mena.wallet.presentation.screen.transaction_history

data class TransactionHistoryScreenState(
    val transactionType: TransactionType,
    val transactionTimeAndDate: String,
    val amount: String,
    val transactionStatus: TransactionStatus,
    val sender: String? = null,
    val receiver: String? = null
) {
    enum class TransactionType {
        PAY, SEND, RECEIVE
    }

    enum class TransactionStatus {
        SUCCESS, FAILED
    }
}