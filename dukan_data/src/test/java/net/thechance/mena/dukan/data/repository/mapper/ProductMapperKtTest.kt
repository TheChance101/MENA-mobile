package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.dto.product.CreateProductRequest
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.data.mapper.toCreateProductRequest
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CreateProductParams
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProductMapperKtTest {

    @Test
    fun `toCreateProductRequest maps params correctly to request object`() {

        val params = CreateProductParams(
            name = "Demo Product",
            description = "A description",
            price = 10.5,
            shelfId = "shelf-123"
        )

        val request: CreateProductRequest = params.toCreateProductRequest()

        assertEquals("Demo Product", request.name)
        assertEquals("A description", request.description)
        assertEquals(10.5, request.price)
        assertEquals("shelf-123", request.shelfId)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toDomain maps dto to domain correctly`() {
        val id = Uuid.random()
        val shelfId = Uuid.random()

        val dto = ProductDto(
            id = id,
            name = "Demo Product",
            description = "A description",
            price = 10.5,
            shelfId = shelfId,
            imageUrls = listOf("url1", "url2"),
            createdAt = "2025-09-26T15:26:41.300823Z"
        )

        val product: Product = dto.toDomain()

        assertEquals(id, product.id)
        assertEquals("Demo Product", product.name)
        assertEquals("A description", product.description)
        assertEquals(10.5, product.price)
        assertEquals(listOf("url1", "url2"), product.imageUrls)
        assertEquals("2025-09-26T15:26:41.300823Z", product.createdAt)
    }

}