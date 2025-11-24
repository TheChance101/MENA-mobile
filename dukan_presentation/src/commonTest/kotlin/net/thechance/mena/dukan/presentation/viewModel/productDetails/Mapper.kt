package net.thechance.mena.dukan.presentation.viewModel.productDetails

import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.toUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProductDetailsMapperTest {

    @OptIn(ExperimentalUuidApi::class)
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
            shelfId = null,
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
    fun `toDomainParams should map ProductInfo to domain params correctly`() {
        val uiProduct = ProductDetailsUiState.ProductInfo(
            id = "123",
            name = "Product",
            description = "Desc",
            basePrice = 9.99,
            images = emptyList(),
            inCartQuantity = 4,
        )

        val params = uiProduct.toDomainParams("dukanId_1")

        assertEquals("123", params.productId)
        assertEquals(4, params.quantity)
        assertEquals("dukanId_1", params.dukanId)
    }

    @Test
    fun `toColor should map String color to Long`() {
        val color = "#432CCD"
        val colorLong = parseHexColor(color = color)
        assertEquals(0xFF432CCD, colorLong)
    }
}