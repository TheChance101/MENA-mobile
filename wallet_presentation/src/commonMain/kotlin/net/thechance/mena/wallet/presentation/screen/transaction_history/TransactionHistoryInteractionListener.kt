package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionHistoryInteractionListener {
    fun onBackClicked()
    fun onTransactionCardClicked(id: Uuid)
    fun onExportClicked()
    fun onFilterClicked()

}