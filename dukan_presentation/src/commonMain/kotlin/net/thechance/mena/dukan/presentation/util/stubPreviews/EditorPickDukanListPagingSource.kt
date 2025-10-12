package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagingSource
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState

object EditorPickDukanItemsListPagingSource :
    BasePagingSource<MainScreenUiState.EditorPickDukanUiState>() {
    override suspend fun onFetchPage(pageNumber: Int): PagedResult<MainScreenUiState.EditorPickDukanUiState> {
        return PagedResult(
            items = fakeDukans(),
            hasPrevious = false,
            hasNext = true,
            currentPage = pageNumber,
            totalPages = 1000,
            totalItems = 1000000
        )
    }
}

fun fakeDukans(): List<MainScreenUiState.EditorPickDukanUiState> {
    return listOf(
        MainScreenUiState.EditorPickDukanUiState(
            id = "1",
            name = "Dukan Market",
            imageUrl = "https://picsum.photos/200/200?1"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "2",
            name = "Fresh & Best",
            imageUrl = "https://picsum.photos/200/200?2"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "3",
            name = "City Dukan",
            imageUrl = "https://picsum.photos/200/200?3"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "4",
            name = "Happy Store",
            imageUrl = "https://picsum.photos/200/200?4"
        )
    )
}