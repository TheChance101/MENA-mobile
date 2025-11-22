@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class DukanRequestsScreenState(
    val dukans: List<DukanItem> = emptyList(),
    val totalDukanRequests: Int = 0,
    val pageInfo: DukanPageInfo = DukanPageInfo(),
    val sort: SortState = SortState(),
    val isLoading: Boolean = false,
    val isInitialLoading: Boolean = true,
    val errorState: ErrorState? = null,
    val snackBar: SnackBarState = SnackBarState(),
    val isRejectDialogShown: Boolean = false,
    val rejectReason: String = "",
    val isRejectButtonLoading: Boolean = false,
    val isDukanDetailsShown: Boolean = false,
    val selectedDukan: DukanItem? = null
) {
    val isSortingDisabled = totalDukanRequests < 2
    val isRejectButtonEnabled: Boolean
        get() = rejectReason.length > 1

    data class DukanPageInfo(
        val page: Int = 0,
        val totalPages: Int = 1
    )

    data class DukanItem(
        val index: Int,
        val id: Uuid,
        val name: String,
        val imageUrl: String,
        val address: String,
        val date: String,
        val categories: List<String> = listOf(),
        val coordinates: CoordinatesUiState = CoordinatesUiState()
    ) {
        data class CoordinatesUiState(
            val latitude: Double = 0.0,
            val longitude: Double = 0.0,
        )
    }

    data class SortState(
        val type: SortType = SortType.DUKAN_NAME,
        val direction: SortDirection = SortDirection.ASC
    )

    enum class SortDirection {
        ASC, DESC;

        fun toggle(): SortDirection = if (this == ASC) DESC else ASC
    }

    enum class SortType {
        DUKAN_NAME, DATE
    }
}
