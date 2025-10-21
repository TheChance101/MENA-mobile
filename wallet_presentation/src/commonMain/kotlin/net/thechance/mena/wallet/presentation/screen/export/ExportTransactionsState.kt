package net.thechance.mena.wallet.presentation.screen.export

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.presentation.model.CustomToastState

import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState

data class ExportTransactionsState(
    val snackBar: SnackBarState = SnackBarState(),
    val toast: CustomToastState = CustomToastState(),
    val isCustomFilterCardSelected: Boolean = false,
    val isDownloadLoading: Boolean = false,
    val isDownloadButtonEnabled: Boolean = true,
    val isViewAndShareLoading: Boolean = false,
    val isViewAndShareButtonEnabled: Boolean = true,
    val filterState: FilterState = FilterState(),
    val dateState: DateState = DateState(),
    val hasNoTransactionsError: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = filterState.startDate != null && filterState.endDate != null && filterState.selectedTransactionsTypes.isNotEmpty()

    data class FilterState(
        val selectedTransactionsTypes: Set<FilterType> = emptySet(),
        val startDate: LocalDate? = null,
        val endDate: LocalDate? = null,
    )

    data class DateState(
        val isDateBottomSheetVisible: Boolean = false,
        val datePickerMode: DatePickerMode = DatePickerMode.START_DATE,
        val defaultStartDate: LocalDate? = null,
        val defaultEndDate: LocalDate? = null,
    )

    enum class DatePickerMode {
        START_DATE,
        END_DATE
    }
}

