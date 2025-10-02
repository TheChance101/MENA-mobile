package net.thechance.mena.wallet.presentation.screen.export

import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType

interface ExportTransactionsListener {
    fun onBackClicked()
    fun onAllTransactionsClicked()
    fun onCustomFilteringClicked()
    fun onTypeSelected(type: FilterType)
    fun onFromDateClicked()
    fun onToDateClicked()
    fun onViewAndShareClicked()
    fun onDownloadClicked()
}