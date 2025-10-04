package net.thechance.mena.wallet.presentation.screen.export

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.presentation.model.FilterType

interface ExportTransactionsListener {
    fun onBackClicked()
    fun onAllTransactionsClicked()
    fun onCustomFilteringClicked()
    fun onTypeSelected(type: FilterType)
    fun onStartDateClicked()
    fun onEndDateClicked()
    fun onDismissDatePicker()
    fun onPickDateClicked(date: LocalDate)
    fun onViewAndShareClicked()
    fun onDownloadClicked()
}