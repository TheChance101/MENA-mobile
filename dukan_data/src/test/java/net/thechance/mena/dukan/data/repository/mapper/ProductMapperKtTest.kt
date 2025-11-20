package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.dto.product.CreateProductRequest
import net.thechance.mena.dukan.data.dto.product.PriceDto
import net.thechance.mena.dukan.data.dto.product.ProductCartDto
import net.thechance.mena.dukan.data.dto.product.ProductDto
import net.thechance.mena.dukan.data.mapper.toCreateProductRequest
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CreateProductParams
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProductMapperKtTest {

    @Test
    fun `toCreateProductRequest maps params correctly to request object`() {

        val params = CreateProductParams(
            name = "Demo Product", description = "A description", price = Price(
                base = 10.5, final = 10.5
            ), shelfId = "shelf-123"
        )

        val request: CreateProductRequest = params.toCreateProductRequest()

        assertEquals("Demo Product", request.name)
        assertEquals("A description", request.description)
        assertEquals(10.5, request.price.base)
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
            price = PriceDto(
                base = 10.5, final = 10.5
            ),
            shelfId = shelfId,
            imageUrls = listOf("url1", "url2"),
            createdAt = "2025-09-26T15:26:41.300823Z",
            quantityInCart = 10
        )

        val product: Product = dto.toDomain()

        assertEquals(id, product.id)
        assertEquals("Demo Product", product.name)
        assertEquals("A description", product.description)
        assertEquals(10.5, product.price.base)
        assertEquals(listOf("url1", "url2"), product.imageUrls)
        assertEquals("2025-09-26T15:26:41.300823Z", product.createdAt)
    }


    private val id = Uuid.random()
    private val dto = ProductCartDto(
        id = id, name = "Demo Product", description = "A description", price = PriceDto(
            base = 10.5, final = 10.5
        ), quantityInCart = 10, imageUrl = "url1"
    )
    private val product = dto.toDomain()


    @Test
    fun `ProductCartDto toDomain id maps correctly`() {
        assertEquals(id, product.id)
    }

    @Test
    fun `ProductCartDto toDomain name maps correctly`() {
        assertEquals("Demo Product", product.name)
    }


    @Test
    fun `ProductCartDto toDomain description maps correctly`() {
        assertEquals("A description", product.description)
    }

    @Test
    fun `ProductCartDto toDomain price maps correctly`() {
        assertEquals(10.5, product.price.base)
    }

    @Test
    fun `ProductCartDto toDomain imageUrls maps correctly`() {
        assertEquals(listOf("url1"), product.imageUrls)
    }


    @Test
    fun `ProductCartDto toDomain quantityInCart maps correctly`() {
        assertEquals(10, product.quantityInCart)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toDomain maps isFavorite correctly when true`() {
        val id = Uuid.random()
        val shelfId = Uuid.random()

        val dto = ProductDto(
            id = id,
            name = "Demo Product",
            description = "A description",
            price = PriceDto(
                base = 10.5, final = 10.5
            ),
            shelfId = shelfId,
            imageUrls = listOf("url1", "url2"),
            createdAt = "2025-09-26T15:26:41.300823Z",
            quantityInCart = 10,
            isFavorite = true
        )

        val product: Product = dto.toDomain()

        assertEquals(true, product.isFavorite)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toDomain maps isFavorite correctly when false`() {
        val id = Uuid.random()
        val shelfId = Uuid.random()

        val dto = ProductDto(
            id = id,
            name = "Demo Product",
            description = "A description",
            price = PriceDto(
                base = 10.5, final = 10.5
            ),
            shelfId = shelfId,
            imageUrls = listOf("url1", "url2"),
            createdAt = "2025-09-26T15:26:41.300823Z",
            quantityInCart = 10,
            isFavorite = false
        )

        val product: Product = dto.toDomain()

        assertEquals(false, product.isFavorite)
    }

    @Test
    fun `ProductCartDto toDomain isFavorite maps correctly`() {
        assertEquals(false, product.isFavorite)
    }
}