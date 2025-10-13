package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.util.pagination.base.BasePagingSource
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductUiState

object FakeProductPagingSource : BasePagingSource<ProductUiState>() {
    override suspend fun onFetchPage(pageNumber: Int): PagedResult<ProductUiState> {
        return PagedResult(
            items = fakeProducts(),
            hasPrevious = false,
            hasNext = true,
            currentPage = pageNumber,
            totalPages = 1000,
            totalItems = 1000000
        )
    }
}

 fun fakeProducts(): List<ProductUiState> {
    return listOf(
        ProductUiState(
            id = "1",
            name = "Wireless Bluetooth Headphones",
            description = "Girls Crochet Tank Top description text here for this product",
            price = 79.99,
            imageUrl = "https://example.com/images/headphones1.jpg"
        ),
        ProductUiState(
            id = "2",
            name = "Smartphone Case",
            description = "Durable protective case for all models",
            price = 19.99,
            imageUrl = "https://example.com/images/case1.jpg"
        ),
        ProductUiState(
            id = "3",
            name = "Stainless Steel Water Bottle",
            price = 24.99,
            imageUrl = "https://example.com/images/bottle1.jpg"
        ),
        ProductUiState(
            id = "4",
            name = "Girls Crochet Tank Top",
            description = "Girls Crochet Tank Top description text here for this product",
            price = 15.99,
            imageUrl = "https://example.com/images/coffee1.jpg"
        ),
        ProductUiState(
            id = "5",
            name = "Girls Crochet Tank Top",
            description = "Girls Crochet Tank Top description text here for this product",
            price = 23.70,
            imageUrl = "https://example.com/images/mat1.jpg"
        )
    )
}