package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultAddOrUpdateProductQuantityResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultDeleteProductFromCartResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.dukanCartRepository
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import org.junit.Test
import kotlin.test.assertTrue

class DukanCartRepositoryImplTest {

    @Test
    fun `Successful product quantity update`() = runTest {
        var called = false
        val repo = dukanCartRepository(
            addOrUpdateProductCartResponse = {
                called = true
                defaultAddOrUpdateProductQuantityResponse()
            }
        )

        repo.updateProductQuantity(
            params = UpdateProductCartQuantityParams(
                dukanId = "10",
                productId = "5",
                quantity = 5
            )
        )
        assertTrue(called)

    }

    @Test
    fun `Successful add product quantity`() = runTest {
        var called = false
        val repo = dukanCartRepository(
            addOrUpdateProductCartResponse = {
                called = true
                defaultDeleteProductFromCartResponse()
            }
        )

        repo.addProductQuantity(
            params = UpdateProductCartQuantityParams(
                dukanId = "10",
                productId = "5",
                quantity = 5
            )
        )
        assertTrue(called)

    }

    @Test
    fun `deleteProduct from cart call success`() = runTest {

        var called = false
        val repo = dukanCartRepository(
            deleteProductFromCartResponse = {
                called = true
                defaultDeleteProductFromCartResponse()
            }
        )

        repo.deleteProductFromCart("10","5")

        assertTrue(called)
    }
}