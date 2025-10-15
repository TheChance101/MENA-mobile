package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagingSource
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState

class FakeShelvesDukanDetailsPagingSource : BasePagingSource<ShelfUiState>() {
    override suspend fun onFetchPage(pageNumber: Int): PagedResult<ShelfUiState> {
        return PagedResult(
            items = fakeShelves(),
            hasPrevious = false,
            hasNext = true,
            currentPage = pageNumber,
            totalPages = 1000,
            totalItems = 1000000
        )
    }
}

val fakeProducts = listOf(
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    )
)

fun fakeShelves(): List<ShelfUiState> {
    return listOf(
        ShelfUiState(
            id = "1",
            name = "Clothes",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "2",
            name = "Perfumes",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "3",
            name = "Accessories",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "4",
            name = "Shoes",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "5",
            name = "Shoes",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "6",
            name = "Shoes",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "7",
            name = "Shoes",
            products = fakeProducts
        ),
        ShelfUiState(
            id = "8",
            name = "Shoes",
            products = fakeProducts
        )
    )
}