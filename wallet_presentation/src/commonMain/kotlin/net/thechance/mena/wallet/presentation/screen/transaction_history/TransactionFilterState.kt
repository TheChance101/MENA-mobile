package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType

data class TransactionFilterState(
    val selectedTypes: Set<FilterType> = emptySet(),
    val selectedStatus: FilterStatus = FilterStatus.ALL,
    val defaultStartDate: LocalDate? = null,
    val defaultEndDate: LocalDate? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val activeFilterCount: Int = 0,
    val isDateBottomSheetVisible: Boolean = false,
    val datePickerMode: DatePickerMode = DatePickerMode.START_DATE,
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null
) {
    val hasActiveFilters: Boolean
        get() = selectedTypes.isNotEmpty() || selectedStatus != FilterStatus.ALL
                || startDate != null || endDate != null

    enum class DatePickerMode {
        START_DATE,
        END_DATE
    }
}