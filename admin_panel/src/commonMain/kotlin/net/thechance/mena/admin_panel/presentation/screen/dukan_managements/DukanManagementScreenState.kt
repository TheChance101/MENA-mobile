package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import net.thechance.mena.admin_panel.domain.model.DukansSortType
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DukanManagementScreenState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isInitialLoading: Boolean = true,
    val pageInfo: PageInfo = PageInfo(),
    val totalDukans: Int = 0,
    val sort: SortState = SortState(),
    val isBlockDialogShown: Boolean = false,
    val snackBar: SnackBarState = SnackBarState(),
    val dukans: List<Dukan> = emptyList(),
    val errorState: ErrorState? = null,
    val selectedDukanId: Uuid? = null
) {
    val isSortingDisabled = totalDukans < 2

    data class Dukan(
        val id: Uuid,
        val index: Int,
        val name: String,
        val imageUrl: String,
        val location: String,
        val addedDate: String,
        val isActive: Boolean = true
    )

    data class SortState(
        val type: DukansSortType = DukansSortType.NAME,
        val direction: SortDirection = SortDirection.ASC
    )

    data class PageInfo(
        val page: Int = 0,
        val totalPages: Int = 1
    )

}