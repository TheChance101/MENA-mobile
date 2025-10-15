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
    val isViewAndShareLoading: Boolean = false,
    val isViewAndShareButtonEnabled: Boolean = true,
    val isDownloadButtonEnabled: Boolean = true,
    val hasShownEmptyFileToast: Boolean = false,
    val noInternetConnection: Boolean = false,
    val selectedTransactionsTypes: Set<FilterType> = emptySet(),
    val isDateBottomSheetVisible: Boolean = false,
    val datePickerMode: DatePickerMode = DatePickerMode.START_DATE,
    val defaultStartDate: LocalDate? = null,
    val defaultEndDate: LocalDate? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val hasNoTransactionsError: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = ((startDate != null || endDate != null) && selectedTransactionsTypes.isNotEmpty())

    enum class DatePickerMode {
        START_DATE,
        END_DATE
    }
}

