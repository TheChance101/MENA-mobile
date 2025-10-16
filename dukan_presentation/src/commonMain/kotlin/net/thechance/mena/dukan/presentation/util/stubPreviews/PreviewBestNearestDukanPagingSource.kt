package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagingSource
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState

object PreviewBestNearestDukanPagingSource :
    BasePagingSource<MainScreenUiState.BestNearestDukanUiState>() {
    override suspend fun onFetchPage(pageNumber: Int): PagedResult<MainScreenUiState.BestNearestDukanUiState> {
        return PagedResult(
            items = fakeBestNearestDuknas(),
            hasPrevious = false,
            hasNext = true,
            currentPage = pageNumber,
            totalPages = 1000,
            totalItems = 1000000
        )
    }
}

fun fakeBestNearestDuknas(): List<MainScreenUiState.BestNearestDukanUiState> {
    return listOf(
        MainScreenUiState.BestNearestDukanUiState(
            id = "1",
            name = "Dukan Market",
            imageUrl = "https://picsum.photos/200/200?1"
        ),
        MainScreenUiState.BestNearestDukanUiState(
            id = "2",
            name = "Fresh & Best",
            imageUrl = "https://picsum.photos/200/200?2"
        ),
        MainScreenUiState.BestNearestDukanUiState(
            id = "3",
            name = "City Dukan",
            imageUrl = "https://picsum.photos/200/200?3"
        ),
        MainScreenUiState.BestNearestDukanUiState(
            id = "4",
            name = "Happy Store",
            imageUrl = "https://picsum.photos/200/200?4"
        )
    )
}