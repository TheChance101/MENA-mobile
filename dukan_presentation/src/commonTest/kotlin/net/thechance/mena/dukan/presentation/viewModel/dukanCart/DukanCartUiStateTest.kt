package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DukanCartUiStateTest {
    @Test
    fun `default state SHOULD be in LOADING state for cart`() = runTest {
        val state = DukanCartUiState()
        assertEquals(DukanCartUiState.CartState.LOADING, state.cartState)
    }

    @Test
    fun `default state SHOULD be in LOADING state for dukan info`() = runTest {
        val state = DukanCartUiState()
        assertEquals(DukanCartUiState.DukanInfoState.LOADING, state.dukanInfoState)
    }

    @Test
    fun `default state SHOULD have emptyFlow for products`() = runTest {
        val state = DukanCartUiState()
        assertEquals(emptyFlow(), state.products)
    }

    @Test
    fun `default state SHOULD have zero total price`() = runTest {
        val state = DukanCartUiState()
        assertEquals(0.0, state.totalPrice)
    }

    @Test
    fun `default state SHOULD have null snackbar`() = runTest {
        val state = DukanCartUiState()
        assertNull(state.snackBarState)
    }

    @Test
    fun `default DukanInfoUiState SHOULD have empty fields`() = runTest {
        val info = DukanCartUiState.DukanInfoUiState()
        assertEquals("", info.id)
        assertEquals("", info.name)
        assertEquals("", info.imageUrl)
    }

    @Test
    fun `default ProductUiState SHOULD have empty fields and zero price`() = runTest {
        val product = DukanCartUiState.ProductUiState()
        assertEquals("", product.id)
        assertEquals("", product.name)
        assertEquals("", product.description)
        assertEquals("", product.imageUrl)
        assertEquals(0.0, product.basePrice)
        assertEquals(0.0, product.finalPrice)
        assertEquals(0, product.quantity)
    }

    @Test
    fun `ProductUiState SHOULD store its properties correctly`() = runTest {
        val product = DukanCartUiState.ProductUiState(
            id = "p1",
            name = "Laptop",
            description = "Gaming Laptop",
            imageUrl = "laptop.png",
            basePrice = 25000.0,
            finalPrice = 10000.0,
            quantity = 2
        )
        assertEquals("p1", product.id)
        assertEquals("Laptop", product.name)
        assertEquals("Gaming Laptop", product.description)
        assertEquals("laptop.png", product.imageUrl)
        assertEquals(25000.0, product.basePrice)
        assertEquals(10000.0, product.finalPrice)
        assertEquals(2, product.quantity)
    }

    @Test
    fun `copy SHOULD update the specified property in the new instance`() = runTest {
        val initial = DukanCartUiState()
        val updated = initial.copy(
            dukanInfo = initial.dukanInfo.copy(name = "Updated Store"),
            totalPrice = 120.0
        )
        assertEquals("Updated Store", updated.dukanInfo.name)
        assertEquals(120.0, updated.totalPrice)
    }

    @Test
    fun `copy SHOULD not mutate the original instance`() = runTest {
        val initial = DukanCartUiState()
        initial.copy(dukanInfo = initial.dukanInfo.copy(name = "Updated Store"))
        assertEquals("", initial.dukanInfo.name)
    }

    @Test
    fun `copy SHOULD produce a different instance`() = runTest {
        val initial = DukanCartUiState()
        val updated = initial.copy(dukanInfo = initial.dukanInfo.copy(name = "Updated Store"))
        assertNotEquals(initial, updated)
    }

    @Test
    fun `copy SHOULD carry over unchanged properties`() = runTest {
        val initial = DukanCartUiState()
        val updated = initial.copy(
            dukanInfo = initial.dukanInfo.copy(name = "Updated Store")
        )
        assertEquals(initial.cartState, updated.cartState)
        assertEquals(initial.dukanInfoState, updated.dukanInfoState)
        assertEquals(initial.products, updated.products)
    }

    @Test
    fun `CartState enum SHOULD contain all expected values`() = runTest {
        val values = DukanCartUiState.CartState.entries.toSet()
        assertTrue(values.contains(DukanCartUiState.CartState.LOADING))
        assertTrue(values.contains(DukanCartUiState.CartState.LOADED))
        assertTrue(values.contains(DukanCartUiState.CartState.NO_INTERNET))
    }

    @Test
    fun `DukanInfoState enum SHOULD contain all expected values`() = runTest {
        val values = DukanCartUiState.DukanInfoState.entries.toSet()
        assertTrue(values.contains(DukanCartUiState.DukanInfoState.LOADING))
        assertTrue(values.contains(DukanCartUiState.DukanInfoState.LOADED))
    }
}