package net.thechance.mena.wallet.presentation.screen.export

import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.pick_end_date
import mena.wallet_presentation.generated.resources.pick_start_date

import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.orToday
import org.jetbrains.compose.resources.StringResource

data class ExportTransactionsState(
    val snackBar: SnackBarState = SnackBarState(),
    val toast: ToastState = ToastState(),
    val isCustomFilterCardSelected: Boolean = false,
    val isDownloadLoading: Boolean = false,
    val isDownloadButtonEnabled: Boolean = true,
    val isViewAndShareLoading: Boolean = false,
    val isViewAndShareButtonEnabled: Boolean = true,
    val filterState: FilterState = FilterState(),
    val dateState: DateState = DateState(),
    val hasNoTransactionsError: Boolean = false,
    val canSelectExportType: Boolean = false
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

    data class ToastState(
        val isVisible: Boolean = false,
        val messageRes: StringResource? = null,
    )

    enum class DatePickerMode(val titleRes: StringResource) {
        START_DATE(Res.string.pick_start_date),
        END_DATE(Res.string.pick_end_date)
    }

    val defaultSelectedDate: LocalDate
        get() = when (dateState.datePickerMode) {
            DatePickerMode.START_DATE ->
                dateState.defaultStartDate.orToday()

            DatePickerMode.END_DATE ->
                dateState.defaultEndDate.orToday()
        }
}

