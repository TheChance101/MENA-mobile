package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DukanDetailsUiStateTest {

    @Test
    fun `default DukanDetailsUiState SHOULD have expected initial values`() = runTest {
        val state = DukanDetailsUiState()

        assertEquals("", state.name)
        assertEquals("", state.imageUrl)
        assertEquals(DukanDetailsUiState.Style.NO_IMAGE, state.style)
        assertEquals(DukanDetailsUiState.ColorUiState(), state.color)
        assertEquals(DukanDetailsUiState.Coordinates(), state.coordinates)
        assertTrue(state.bestSellingProducts.isEmpty())
        assertTrue(state.shelves.isEmpty())
    }

    @Test
    fun `Coordinates SHOULD store latitude and longitude correctly`() = runTest {
        val coords = DukanDetailsUiState.Coordinates(latitude = 30.0, longitude = 31.0)

        assertEquals(30.0, coords.latitude)
        assertEquals(31.0, coords.longitude)
    }

    @Test
    fun `ShelfUiState SHOULD contain products`() = runTest {
        val product = DukanDetailsUiState.ProductUiState(
            id = "p1",
            name = "Banana",
            imageUrl = "banana.png",
            price = 5.0,
            description = "Fresh bananas",
            shelfId = "s1"
        )
        val shelf = DukanDetailsUiState.ShelfUiState(
            id = "s1",
            name = "Fruits",
            products = listOf(product)
        )

        assertEquals("Fruits", shelf.name)
        assertEquals(1, shelf.products.size)
        assertEquals(product, shelf.products.first())
    }

    @Test
    fun `ColorUiState SHOULD store id and color correctly`() = runTest {
        val color = DukanDetailsUiState.ColorUiState(
            id = "c1",
            color = 0xFF00FF
        )

        assertEquals("c1", color.id)
        assertEquals(0xFF00FF, color.color)
    }

    @Test
    fun `copy SHOULD create new instance with updated values`() = runTest {
        val initial = DukanDetailsUiState(name = "Old Name")
        val updated = initial.copy(name = "New Name")

        assertEquals("New Name", updated.name)
        assertEquals("Old Name", initial.name) // immutability check
    }
}