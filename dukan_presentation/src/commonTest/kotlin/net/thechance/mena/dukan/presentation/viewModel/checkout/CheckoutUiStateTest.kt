package net.thechance.mena.dukan.presentation.viewModel.checkout

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi

class CheckoutUiStateTest {

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `default state SHOULD have empty values`() = runTest {
        val state = CheckoutUiState()
        assertEquals(CheckoutUiState.Address(), state.deliveryAddress)
        assertEquals(emptyFlow(), state.items)
        assertEquals(0.0, state.cartDetails.discountPercentage)
        assertEquals(0.0, state.cartDetails.platformFees)
        assertEquals(0.0, state.cartDetails.totalPriceAfterDiscount)
    }

    @Test
    fun `Address SHOULD store its properties correctly`() = runTest {
        val address = CheckoutUiState.Address(
            label = CheckoutUiState.AddressLabel.Home,
            street = "123 Street Name"
        )
        assertEquals(CheckoutUiState.AddressLabel.Home, address.label)
        assertEquals("123 Street Name", address.street)
    }

    @Test
    fun `CartItem SHOULD store its properties correctly`() = runTest {
        val item = CheckoutUiState.CartItem(
            id = "c1",
            name = "Apple",
            quantity = 3,
            price = 10.0
        )
        assertEquals("c1", item.id)
        assertEquals("Apple", item.name)
        assertEquals(3, item.quantity)
        assertEquals(10.0, item.price)
    }
}