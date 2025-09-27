package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.domain.entity.Transaction

data class TransactionHistoryScreenState(
    val type: Transaction.Type,
    val timeAndDate: String,
    val amount: String,
    val status: Transaction.Status,
    val sender: String? = null,
    val receiver: String? = null
)