package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShelfDetailsUiStateTest {

    @Test
    fun `default state SHOULD have empty PagingData for products shelf`() = runTest {
        val state = ShelfDetailsUiState()
        val data = state.productsShelf
        assertEquals(data, emptyFlow())
    }

    @Test
    fun `ProductUiState SHOULD store its properties correctly`() = runTest {
        val product = ShelfDetailsUiState.ProductUiState(
            id = "p1",
            name = "Banana",
            imageUrl = "banana.png",
            basePrice = 5.0,
            description = "Fresh bananas"
        )
        assertEquals("p1", product.id)
        assertEquals("Banana", product.name)
    }
}