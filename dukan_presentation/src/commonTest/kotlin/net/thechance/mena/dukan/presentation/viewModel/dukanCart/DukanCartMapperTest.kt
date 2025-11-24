package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DukanCartMapperTest {
    @Test
    fun `toUiState should map Product correctly`() {
        val product = Product(
            id = Uuid.random(),
            name = "Headphones",
            description = "Wireless Bluetooth Headphones",
            price = Price(
                base = 999.99,
                final = 999.99
            ),
            imageUrls = listOf("headphones.png"),
            quantityInCart = 2,
            createdAt = "2023-01-01",
            shelfId = null,
            isFavorite = false
        )

        val uiState = product.toUiState()

        assertEquals(product.id.toString(), uiState.id)
        assertEquals("Headphones", uiState.name)
        assertEquals("Wireless Bluetooth Headphones", uiState.description)
        assertEquals(999.99, uiState.basePrice, 0.0)
        assertEquals(999.99, uiState.finalPrice, 0.0)
        assertEquals("headphones.png", uiState.imageUrl)
        assertEquals(2, uiState.quantity)
    }

    @Test
    fun `toUiState should handle empty imageUrls list`() {
        val product = Product(
            id = Uuid.random(),
            name = "Book",
            description = "E-book",
            price = Price(
                base = 50.0,
                final = 50.0
            ),
            imageUrls = emptyList(),
            quantityInCart = 1,
            createdAt = "2023-01-01",
            shelfId = null,
            isFavorite = false
        )

        val uiState = product.toUiState()

        assertEquals("", uiState.imageUrl)
    }

    @Test
    fun `toUiState should map Dukan correctly`() {
        val dukan = Dukan(
            id = Uuid.random(),
            name = "Tech Store",
            imageUrl = "dukan.png",
            coordinates = Dukan.Coordinates(latitude = 30.1, longitude = 31.3),
            color = Color(id = Uuid.random(), hexCode = "#FF5733"),
            style = Dukan.Style.SMALL_IMAGE,
            status = Dukan.Status.APPROVED,
            address = "Cairo, Egypt",
            categories = emptySet(),
            isFavorite = false
        )

        val uiState = dukan.toUiState()

        assertEquals(dukan.id.toString(), uiState.id)
        assertEquals("Tech Store", uiState.name)
        assertEquals("dukan.png", uiState.imageUrl)
    }
}