package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.pick_end_date
import mena.wallet_presentation.generated.resources.pick_start_date
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.utils.orToday
import org.jetbrains.compose.resources.StringResource

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
    val isApplyButtonLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val oldFilterHash: Int = EMPTY_FILTER_HASH
) {
    val isApplyButtonEnabled: Boolean
        get() = oldFilterHash != currentFilterHash

    val currentFilterHash: Int
        get() = listOf(
            selectedTypes,
            selectedStatus,
            startDate,
            endDate
        ).hashCode()

    enum class DatePickerMode(val titleRes: StringResource) {
        START_DATE(Res.string.pick_start_date),
        END_DATE(Res.string.pick_end_date)
    }

    val defaultSelectedDate: LocalDate
        get() = when (datePickerMode) {
            DatePickerMode.START_DATE ->
                defaultStartDate.orToday()

            DatePickerMode.END_DATE ->
                defaultEndDate.orToday()
        }

    companion object {
        private val EMPTY_FILTER_HASH = listOf(
            emptySet<FilterType>(),
            FilterStatus.ALL,
            null,
            null
        ).hashCode()
    }
}