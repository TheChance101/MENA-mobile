package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import kotlin.test.Test
import kotlin.test.assertEquals

class ShelfDetailsUiStateTest {

    @Test
    fun `default state SHOULD have empty PagingData for products shelf`() = runTest {
        val state = ShelfDetailsUiState()
        assertEquals(PagingData(), state.productsShelf)
    }

    @Test
    fun `default state SHOULD have LOADING productsState`() = runTest {
        val state = ShelfDetailsUiState()
        assertEquals(ShelfDetailsUiState.ProductsState.LOADING, state.productsState)
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