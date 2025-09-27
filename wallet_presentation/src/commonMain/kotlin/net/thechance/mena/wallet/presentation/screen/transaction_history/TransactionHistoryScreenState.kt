package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.domain.entity.Transaction

data class TransactionHistoryScreenState(
    val transactionType: Transaction.Type,
    val transactionTimeAndDate: String,
    val amount: String,
    val transactionStatus: Transaction.Status,
    val sender: String? = null,
    val receiver: String? = null
)