package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DukanDetailsMapperTest {

    @Test
    fun `toUiState should map Dukan correctly`() {
        val color = Color(id = Uuid.random(), hexCode = "#FF5733")
        val dukan = Dukan(
            id = Uuid.random(),
            name = "Test Dukan",
            imageUrl = "dukan.jpg",
            coordinates = Dukan.Coordinates(latitude = 30.05, longitude = 31.2),
            color = color,
            style = Dukan.Style.SMALL_IMAGE,
            status = Dukan.Status.APPROVED,
            address = "Test Address",
            categories = emptySet(),
            isFavorite = false
        )

        val uiState = dukan.toUiState()

        assertEquals("Test Dukan", uiState.name)
        assertEquals("dukan.jpg", uiState.imageUrl)
        assertEquals(30.05, uiState.coordinates.latitude, 0.0)
        assertEquals(31.2, uiState.coordinates.longitude, 0.0)
        assertEquals(0xFFFF5733, uiState.color)
        assertEquals(DukanDetailsUiState.Style.SMALL_IMAGE, uiState.style)
    }

    @Test
    fun `toUiState should map Shelf correctly`() {
        val shelf = Shelf(
            id = Uuid.random(),
            name = "Shoes",
        )

        val uiState = shelf.toUiState()

        assertEquals(shelf.id.toString(), uiState.id)
        assertEquals("Shoes", uiState.name)
    }

    @Test
    fun `toUiState should map Product correctly`() {
        val product = Product(
            id = Uuid.random(),
            name = "Phone",
            description = "Android device",
            price = Price(
                base = 15000.0,
                final = 15000.0
            ),
            imageUrls = listOf("img.png"),
            quantityInCart = 0,
            createdAt = "2023-01-01",
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
            isFavorite = false
        )

        val uiState = product.toUiState()

        assertEquals("Phone", uiState.name)
        assertEquals("Android device", uiState.description)
        assertEquals(15000.0, uiState.basePrice, 0.0)
        assertEquals(15000.0, uiState.finalPrice, 0.0)
        assertEquals("img.png", uiState.imageUrl)
        assertEquals(0, uiState.inCartQuantity)
    }

    @Test
    fun `toUiState should map Product correctly when quantityInCart greater than zero`() {
        val product = Product(
            id = Uuid.random(),
            name = "Laptop",
            description = "Gaming",
            price = Price(
                base = 20000.0,
                final = 20000.0
            ),
            imageUrls = listOf("laptop.png"),
            quantityInCart = 3,
            createdAt = "2023-01-01",
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
            isFavorite = false
        )

        val uiState = product.toUiState()

        assertEquals(3, uiState.inCartQuantity)
    }

    @Test
    fun `toDomainParams should map ProductUiState to domain params correctly`() {
        val uiProduct = DukanDetailsUiState.ProductUiState(
            id = "10",
            name = "Tablet",
            description = "Description",
            basePrice = 10000.0,
            imageUrl = "tablet.png",
            inCartQuantity = 5
        )

        val result = uiProduct.toDomainParams("dukanId_1")

        assertEquals("10", result.productId)
        assertEquals(5, result.quantity)
        assertEquals("dukanId_1", result.dukanId)
    }
}
