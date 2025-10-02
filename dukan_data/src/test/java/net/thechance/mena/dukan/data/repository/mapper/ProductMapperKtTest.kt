package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.product.CreateProductRequest
import net.thechance.mena.dukan.data.repository.dto.product.ProductDto
import net.thechance.mena.dukan.domain.entity.Product
import org.junit.Test
import kotlin.test.assertEquals

class ProductMapperKtTest {

    @Test
    fun `toCreateProductRequest maps domain product to request with shelfId`() {
        val product = Product(
            id = "p1",
            name = "Demo Product",
            description = "A description",
            price = 10.5,
            imageUrls = listOf("url1"),
            createdAt = "2025-09-26T15:26:41.300823Z"
        )

        val request: CreateProductRequest = product.toCreateProductRequest("shelf-123")

        assertEquals("Demo Product", request.name)
        assertEquals("A description", request.description)
        assertEquals(10.5, request.price)
        assertEquals("shelf-123", request.shelfId)
    }

    @Test
    fun `toDomain maps dto to domain correctly`() {
        val dto = ProductDto(
            id = "p1",
            name = "Demo Product",
            description = "A description",
            price = 10.5,
            shelfId = "shelf-123",
            imageUrls = listOf("url1", "url2"),
            createdAt = "2025-09-26T15:26:41.300823Z"
        )

        val product: Product = dto.toDomain()

        assertEquals("p1", product.id)
        assertEquals("Demo Product", product.name)
        assertEquals("A description", product.description)
        assertEquals(10.5, product.price)
        assertEquals(listOf("url1", "url2"), product.imageUrls)
        assertEquals("2025-09-26T15:26:41.300823Z", product.createdAt)
    }

}