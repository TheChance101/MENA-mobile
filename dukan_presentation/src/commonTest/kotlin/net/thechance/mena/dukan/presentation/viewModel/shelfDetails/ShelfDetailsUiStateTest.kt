package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.paging.PagingData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShelfDetailsUiStateTest {

    @Test
    fun `default state SHOULD have empty PagingData for products shelf`() = runTest {
        val state = ShelfDetailsUiState()
        val data = state.productsShelf.first()
        assertTrue(data == PagingData.empty<DukanDetailsUiState.ProductUiState>())
    }

    @Test
    fun `ProductUiState SHOULD store its properties correctly`() = runTest {
        val product = ShelfDetailsUiState.ProductUiState(
            id = "p1",
            name = "Banana",
            imageUrl = "banana.png",
            price = 5.0,
            description = "Fresh bananas"
        )
        assertEquals("p1", product.id)
        assertEquals("Banana", product.name)
    }
}