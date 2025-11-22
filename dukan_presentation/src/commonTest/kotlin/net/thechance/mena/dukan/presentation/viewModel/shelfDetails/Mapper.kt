package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ShelfDetailsMapperTest {

    @Test
    fun `toUiState should map Product correctly`() {
        val product = Product(
            id = Uuid.random(),
            name = "Test Product",
            description = "Description",
            price = Price(
                base = 10.0,
                final = 10.0
            ),
            imageUrls = listOf("image.png"),
            quantityInCart = 0,
            createdAt = "2023-01-01",
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
            isFavorite = false
        )

        val uiState = product.toUiState()

        assertEquals(product.name, uiState.name)
        assertEquals(product.description, uiState.description)
        assertEquals(product.price.base, uiState.basePrice, 0.0)
        assertEquals("image.png", uiState.imageUrl)
        assertEquals(0, uiState.inCartQuantity)
    }

    @Test
    fun `toUiState should map Product correctly when quantityInCart is greater than zero`() {
        val product = Product(
            id = Uuid.random(),
            name = "Another Product",
            description = "Test Desc",
            price = Price(
                base = 50.0,
                final = 50.0
            ),
            imageUrls = listOf("img.jpg"),
            quantityInCart = 3,
            createdAt = "2023-01-01",
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
            isFavorite = false
        )

        val uiState = product.toUiState()

        assertEquals(3, uiState.inCartQuantity)
        assertEquals("img.jpg", uiState.imageUrl)
    }

    @Test
    fun `toDomainParams should map ProductUiState to domain params correctly`() {
        val uiProduct = ShelfDetailsUiState.ProductUiState(
            id = "123",
            name = "Product",
            description = "Desc",
            basePrice = 9.99,
            imageUrl = "img.png",
            inCartQuantity = 4
        )

        val params = uiProduct.toDomainParams("dukanId_1")

        assertEquals("123", params.productId)
        assertEquals(4, params.quantity)
        assertEquals("dukanId_1", params.dukanId)
    }

    @Test
    fun `toShelfStyle should map Dukan Style correctly`() {
        assertEquals(
            ShelfDetailsUiState.Style.NO_IMAGE,
            Dukan.Style.NO_IMAGE.toShelfStyle()
        )
        assertEquals(
            ShelfDetailsUiState.Style.SMALL_IMAGE,
            Dukan.Style.SMALL_IMAGE.toShelfStyle()
        )
        assertEquals(
            ShelfDetailsUiState.Style.WIDE_IMAGE,
            Dukan.Style.WIDE_IMAGE.toShelfStyle()
        )
    }

    @Test
    fun `toUiColor should map Color entity to ColorUiState correctly`() {
        val color = Color(
            id = Uuid.random(),
            hexCode = "#FF5733"
        )

        val result = color.toUiColor()

        assertEquals(color.id.toString(), result.id)
        assertEquals(0xFFFF5733, result.color)
    }
}
