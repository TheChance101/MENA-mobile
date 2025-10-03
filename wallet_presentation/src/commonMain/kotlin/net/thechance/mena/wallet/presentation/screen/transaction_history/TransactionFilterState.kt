package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType

data class TransactionFilterState(
    val selectedTypes: Set<FilterType> = emptySet(),
    val selectedStatus: FilterStatus = FilterStatus.ALL,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val activeFilterCount: Int = 0,
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null
) {
    val hasActiveFilters: Boolean
        get() = selectedTypes.isNotEmpty() || selectedStatus != FilterStatus.ALL
                || fromDate != null || toDate != null
}