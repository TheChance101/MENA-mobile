package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagingSource
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukanUiState

class FakeDukanPagingSource : BasePagingSource<DukanUiState>() {
    override suspend fun onFetchPage(pageNumber: Int): PagedResult<DukanUiState> {
        return PagedResult(
            items = fakeDukansList(),
            hasPrevious = false,
            hasNext = true,
            currentPage = pageNumber,
            totalPages = 1000,
            totalItems = 1000000
        )
    }
}

fun fakeDukansList(): List<DukanUiState> {
    return listOf(
        DukanUiState(
            id = "1",
            name = "Fashion Store",
            imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400"
        ),
        DukanUiState(
            id = "2",
            name = "Electronics Shop",
            imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400"
        ),
        DukanUiState(
            id = "3",
            name = "Sports Equipment Store",
            imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400"
        ),
        DukanUiState(
            id = "4",
            name = "Home Decor Shop",
            imageUrl = "https://images.unsplash.com/photo-1581044777550-4cfa607037dc?w=400"
        ),
        DukanUiState(
            id = "5",
            name = "Shoe Store",
            imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400"
        )
    )
}
