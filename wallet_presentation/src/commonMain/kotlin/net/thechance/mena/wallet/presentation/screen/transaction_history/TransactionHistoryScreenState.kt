package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.base.UiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TransactionHistoryScreenState(
    val history: UiState<List<TransactionHistoryUiState>> = UiState.Idle
){
    data class TransactionHistoryUiState @OptIn(ExperimentalUuidApi::class) constructor(
        val senderId: Uuid,
        val type: Transaction.Type = Transaction.Type.ONLINE_PURCHASE,
        val timeAndDate: String = "5:00 Sep 2025",
        val amount: String = "22222.23",
        val status: Transaction.Status = Transaction.Status.SUCCESS,
        val sender: String? = null,
        val receiver: String? = null
    )
}