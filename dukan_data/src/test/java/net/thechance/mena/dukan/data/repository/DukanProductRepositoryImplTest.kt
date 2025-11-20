package net.thechance.mena.dukan.data.repository

import io.ktor.client.engine.mock.respond
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.jsonHeaders
import net.thechance.mena.dukan.data.repository.mockEngine.product.createProductRepository
import net.thechance.mena.dukan.data.repository.mockEngine.product.createdProductResponseId
import net.thechance.mena.dukan.data.repository.mockEngine.product.defaultCreateProductResponse
import net.thechance.mena.dukan.data.repository.mockEngine.product.defaultProductByIdResponse
import net.thechance.mena.dukan.data.repository.mockEngine.product.defaultUploadImageResponse
import net.thechance.mena.dukan.data.repository.mockEngine.product.demoPagedResult
import net.thechance.mena.dukan.data.repository.mockEngine.product.dummyImageUrls
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.model.CreateProductParams
import net.thechance.mena.dukan.domain.model.UpdateProductParams
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DukanProductRepositoryImplTest {
    private val repository = createProductRepository()

    @Test
    fun `createProduct calls correct endpoint and returns created product ID`() = runTest {
        var called = false
        val repo = createProductRepository(
            createResponse = {
                called = true
                defaultCreateProductResponse()
            }
        )

        val responseProductId = repo.createProduct(
            params = CreateProductParams(
                name = "new product",
                description = "new product description",
                price = Price(
                    base = 12.2,
                    final = 12.2
                ),
                shelfId = "shelf-123"
            )
        )

        assertTrue(called, "Expected the mock engine to be called")
        assertTrue(responseProductId.isNotEmpty(), "Expected non-empty productId")
        assertTrue(
            responseProductId == createdProductResponseId,
            "Expected correct mocked productId"
        )
    }


    @Test
    fun `getProductsByShelfId returns mapped products`() = runTest {
        val products = repository.getProductsByShelfId(
            "shelf-123", 0, 10
        )
        assertEquals(expected = demoPagedResult, actual = products)
    }

    @Test
    fun `uploadProductImages returns list of image URLs`() = runTest {
        val fileNames = listOf("image1.jpg", "image2.jpg")
        val fileBytes = listOf(byteArrayOf(1, 2), byteArrayOf(3, 4))

        val urls = repository.uploadProductImages(
            fileName = fileNames,
            fileBytes = fileBytes,
            productId = createdProductResponseId
        )

        assertEquals(expected = dummyImageUrls, actual = urls)
    }

    @Test
    fun `uploadProductImages throws exception when lists have different sizes`() = runTest {
        val fileNames = listOf("image1.jpg")
        val fileBytes = listOf(byteArrayOf(1, 2), byteArrayOf(3, 4))

        assertFailsWith<IllegalArgumentException> {
            repository.uploadProductImages(
                fileName = fileNames,
                fileBytes = fileBytes,
                productId = createdProductResponseId
            )
        }
    }

    @Test
    fun `getProductById returns mapped product`() = runTest {
        var called = false
        val repo = createProductRepository(
            productByIdResponse = {
                called = true
                defaultProductByIdResponse()
            }
        )

        val product = repo.getProductById("product-123")

        assertTrue(called, "Expected the mock engine to be called")
        assertEquals("Demo Product 1", product.name)
        assertEquals(9.99, product.price.base)
        assertEquals("This is a demo product", product.description)
    }

    @Test
    fun `updateProduct calls correct endpoint`() = runTest {
        var called = false
        val repo = createProductRepository(
            updateResponse = {
                called = true
                respond("", io.ktor.http.HttpStatusCode.OK, jsonHeaders)
            }
        )

        repo.updateProduct(
            productId = "product-123",
            params = UpdateProductParams(
                name = "Updated Product",
                price = Price(
                    base = 15.99,
                    final = 15.99
                ),
                description = "Updated description",
                shelfId = "shelf-123",
                imageUrls = listOf("image1.jpg")
            )
        )

        assertTrue(called, "Expected the mock engine to be called")
    }

    @Test
    fun `deleteProduct calls correct endpoint`() = runTest {
        var called = false
        val repo = createProductRepository(
            deleteResponse = {
                called = true
                respond("", io.ktor.http.HttpStatusCode.OK, jsonHeaders)
            }
        )

        repo.deleteProduct("product-123")

        assertTrue(called, "Expected the mock engine to be called")
    }

    @Test
    fun `uploadProductImages returns mapped image URLs`() = runTest {
        val repo = createProductRepository()

        val imageUrls = repo.uploadProductImages(
            fileName = listOf("image1.jpg", "image2.jpg"),
            fileBytes = listOf(ByteArray(10), ByteArray(20)),
            productId = createdProductResponseId
        )

        assertTrue(imageUrls.isNotEmpty(), "Expected non-empty image URLs")
        assertEquals(2, imageUrls.size)
    }

    @Test
    fun `deleteProductImages calls correct endpoint`() = runTest {
        var called = false
        val repo = createProductRepository(
            deleteImagesResponse = {
                called = true
                respond("", io.ktor.http.HttpStatusCode.OK, jsonHeaders)
            }
        )

        repo.deleteProductImages(
            productId = "product-123",
            imageUrls = listOf("image1.jpg", "image2.jpg")
        )

        assertTrue(called, "Expected the mock engine to be called")
    }

    @Test
    fun `toggleProductToFavorites calls correct endpoint`() = runTest {
        var called = false
        val repo = createProductRepository(
            toggleFavoriteResponse = {
                called = true
                respond("", io.ktor.http.HttpStatusCode.OK, jsonHeaders)
            }
        )

        repo.toggleProductToFavorites("product-123")

        assertTrue(called, "Expected the mock engine to be called")
    }

    @Test
    fun `upload product image calls correct endpoint and returns url`() = runTest {
        val repository = createProductRepository(
            uploadImageResponse = {
                defaultUploadImageResponse()
            }
        )

        val fileName = "image1.jpg"
        val fileBytes = byteArrayOf(1, 2, 3)
        val productId = createdProductResponseId

        val result = repository.uploadProductImage(
            fileName = fileName,
            fileBytes = fileBytes,
            productId = productId
        )

        assertEquals(
            expected = "http://example.com/image1.jpg",
            actual = result.trim('"')
        )
    }
}