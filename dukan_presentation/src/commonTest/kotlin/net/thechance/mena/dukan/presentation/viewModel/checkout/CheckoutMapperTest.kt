package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutMapperTest {

    @Test
    fun `toUiState should map ProductCart to CartItem correctly`() {
        val productCart = Product(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
            name = "test product name",
            price = Price(
                base = 19.99,
                final = 19.99
            ),
            quantityInCart = 3,
            imageUrls = listOf("test image url"),
            description = "test description",
            shelfId = null,
            createdAt = "",
            isFavorite = false
        )

        val cartItem = productCart.toUiState()

        assertEquals(productCart.id.toString(), cartItem.id)
        assertEquals(productCart.name, cartItem.name)
        assertEquals(productCart.price.base, cartItem.price)
        assertEquals(productCart.quantityInCart, cartItem.quantity)
    }
}