package net.thechance.mena.wallet.presentation.screen.transactiondetails

data class TransactionDetailsScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val transaction: Transaction = Transaction(),
    val isShareLoading: Boolean = false,
    val isShareError: String? = null,
){
    data class Transaction(
        val id: String = "",
        val amount: String = "",
        val date: String = "",
        val otherParty: String = "",
        val transactionType: TransactionType = TransactionType.PAY,
    )

    enum class TransactionType{
        PAY,
        SEND,
        RECEIVE
    }
}