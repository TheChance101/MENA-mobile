package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan.createMediaRepository
import net.thechance.mena.dukan.data.repository.mockEngine.product.createProductRepository
import net.thechance.mena.dukan.data.repository.mockEngine.product.createdProductResponseId
import net.thechance.mena.dukan.data.repository.mockEngine.product.defaultCreateProductResponse
import net.thechance.mena.dukan.data.repository.mockEngine.product.demoPagedResult
import net.thechance.mena.dukan.domain.model.CreateProductParams
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DukanProductRepositoryImplTest {
    private val repository = createProductRepository()
    private val mediaRepository = createMediaRepository()

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
                price = 12.2,
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

}
