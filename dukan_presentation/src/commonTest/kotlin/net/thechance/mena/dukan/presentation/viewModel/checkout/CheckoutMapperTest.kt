package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.ProductCart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutMapperTest {

    @Test
    fun `toUiState should map ProductCart to CartItem correctly`() {
        val productCart = ProductCart(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
            name = "test product name",
            price = 19.99,
            quantity = 3,
            imageUrl = "test image url",
            description ="test description"
        )

        val cartItem = productCart.toUiState()

        assertEquals(productCart.id.toString(), cartItem.id)
        assertEquals(productCart.name, cartItem.name)
        assertEquals(productCart.price, cartItem.price)
        assertEquals(productCart.quantity, cartItem.quantity)
    }
}