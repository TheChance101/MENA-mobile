package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DukanDetailsUiStateTest {

    @Test
    fun `default state SHOULD be in loading state for dukan info`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertTrue(state.isDukanInfoLoading)
    }

    @Test
    fun `default state SHOULD have empty PagingData for best selling products`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(emptyFlow(), state.bestSellingProducts)
    }

    @Test
    fun `default state SHOULD have empty PagingData for shelves`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(emptyFlow(), state.shelves)
    }

    @Test
    fun `default state SHOULD have null shelfIdSelected`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertNull(state.shelfIdSelected)
    }

    @Test
    fun `default state SHOULD have empty dukan name`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals("", state.dukanInfo.name)
    }

    @Test
    fun `default state SHOULD have empty dukan imageUrl`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals("", state.dukanInfo.imageUrl)
    }

    @Test
    fun `default state SHOULD have NO_IMAGE dukan style`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(DukanDetailsUiState.Style.NO_IMAGE, state.dukanInfo.style)
    }

    @Test
    fun `default state SHOULD have zero dukan color`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(0L, state.dukanInfo.color)
    }

    @Test
    fun `default state SHOULD have default dukan coordinates`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(DukanDetailsUiState.Coordinates(), state.dukanInfo.coordinates)
    }

    @Test
    fun `default state SHOULD have false dukan has product in cart`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(false, state.hasProductInCart)
    }

    @Test
    fun `default state SHOULD have default dukan shelfProductsLimited`() = runTest {
        // When
        val state = DukanDetailsUiState()
        // Then
        assertEquals(emptyMap(), state.shelfProductsLimited)
    }


    @Test
    fun `Coordinates SHOULD store latitude and longitude correctly`() = runTest {
        // Given
        val coords = DukanDetailsUiState.Coordinates(latitude = 30.0, longitude = 31.0)
        // Then
        assertEquals(30.0, coords.latitude)
        assertEquals(31.0, coords.longitude)
    }

    @Test
    fun `ShelfUiState SHOULD store id and name correctly`() = runTest {
        // Given
        val shelf = DukanDetailsUiState.ShelfUiState(id = "s1", name = "Fruits")
        // Then
        assertEquals("s1", shelf.id)
        assertEquals("Fruits", shelf.name)
    }

    @Test
    fun `ProductUiState SHOULD store its properties correctly`() = runTest {
        // Given
        val product = DukanDetailsUiState.ProductUiState(
            id = "p1",
            name = "Banana",
            imageUrl = "banana.png",
            basePrice = 5.0,
            finalPrice = 3.0,
            description = "Fresh bananas"
        )
        // Then
        assertEquals("p1", product.id)
        assertEquals("Banana", product.name)
        assertEquals(5.0, product.basePrice)
        assertEquals(3.0, product.finalPrice)
    }

    @Test
    fun `copy SHOULD update the specified property in the new instance`() = runTest {
        // Given
        val initial = DukanDetailsUiState()
        // When
        val updated = initial.copy(
            dukanInfo = initial.dukanInfo.copy(name = "New Dukan Name")
        )
        // Then
        assertEquals("New Dukan Name", updated.dukanInfo.name)
    }

    @Test
    fun `copy SHOULD not mutate the original instance`() = runTest {
        // Given
        val initial = DukanDetailsUiState()
        // When
        initial.copy(dukanInfo = initial.dukanInfo.copy(name = "New Dukan Name"))
        // Then
        assertEquals("", initial.dukanInfo.name)
    }

    @Test
    fun `copy SHOULD produce a different instance`() = runTest {
        // Given
        val initial = DukanDetailsUiState()
        // When
        val updated = initial.copy(
            dukanInfo = initial.dukanInfo.copy(name = "New Dukan Name")
        )
        // Then
        assertNotEquals(initial, updated)
    }

    @Test
    fun `copy SHOULD carry over properties that were not changed`() = runTest {
        // Given
        val initial = DukanDetailsUiState()
        // When
        val updated = initial.copy(
            dukanInfo = initial.dukanInfo.copy(name = "New Dukan Name")
        )
        // Then
        assertEquals(initial.isDukanInfoLoading, updated.isDukanInfoLoading)
    }
}