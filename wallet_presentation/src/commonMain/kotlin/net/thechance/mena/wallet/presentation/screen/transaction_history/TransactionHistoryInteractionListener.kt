package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDate
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
    fun onNextPageRequested()
    fun onDismissFilter()
    fun onFilterTypeSelected(type: FilterType)
    fun onFilterStatusSelected(status: FilterStatus)
    fun onResetFilterClicked()
    fun onApplyFilterClicked()
    fun onStartDateClicked()
    fun onEndDateClicked()
    fun onDismissDatePicker()
    fun onPickDateClicked(date: LocalDate)
    fun onRetryLoadTransactionHistoryClicked()
}