package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DukanManagementScreenState(
    val query: String = "",
    val isLoading: Boolean = false,
    val pageInfo: PageInfo = PageInfo(),
    val sort: SortState = SortState(),
    val isBlockDialogShown: Boolean = false,
    val snackBar: SnackBarState = SnackBarState(),
    val dukanCounts: String = "",
    val dukans: List<Dukan> = emptyList(),
    val errorState: ErrorState? = null,
    val selectedDukanId: Uuid? = null
) {
    data class Dukan(
        val id: Uuid,
        val name: String,
        val location: String,
        val addedDate: String,
        val isActive: Boolean = true
    )

    data class SortState(
        val type: SortType = SortType.DUKAN_NAME,
        val direction: SortDirection = SortDirection.ASC
    )

    enum class SortDirection {
        ASC, DESC;

        fun toggle(): SortDirection = if (this == ASC) DESC else ASC
    }

    enum class SortType {
        DUKAN_NAME, STATUS, ADDED_DATE
    }

    data class PageInfo(
        val page: Int = 0,
        val totalPages: Int = 1
    )

}