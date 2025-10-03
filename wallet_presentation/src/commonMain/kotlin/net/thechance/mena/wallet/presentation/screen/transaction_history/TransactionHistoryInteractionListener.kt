package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionHistoryInteractionListener {
    fun onBackClicked()
    fun onTransactionCardClicked(id: Uuid)
    fun onExportClicked()
    fun onFilterClicked()
    fun onDismissFilter()
    fun selectFilterType(type: FilterType)
    fun selectFilterStatus(status: FilterStatus)
    fun onResetFilterClicked()
    fun onApplyFilterClicked()
    fun onRetryLoadTransactionHistoryClicked()
}