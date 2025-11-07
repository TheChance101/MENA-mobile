package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.cart1
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultAddOrUpdateProductQuantityResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultCartInfoResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultDeleteProductFromCartResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultProductCartResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.demoPagedResultProductCart
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.dukanCartRepository
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DukanCartRepositoryImplTest {

    val dukanId = Uuid.parse("123e4567-e89b-12d3-a456-426614174003")

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
                dukanId = dukanId.toString(),
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
                dukanId = dukanId.toString(),
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

        repo.deleteProductFromCart(dukanId.toString(), "5")

        assertTrue(called)
    }

    @Test
    fun `get cart info call success`() = runTest {
        val repo = dukanCartRepository(
            getCartInfoResponse = { defaultCartInfoResponse() }
        )

        val cartInfo = repo.getCartInfo(dukanId.toString())

        assertEquals(expected = cart1, actual = cartInfo)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `getProductsCart returns mapped products`() = runTest {
        val repo = dukanCartRepository(
            productCartResponse = {
                defaultProductCartResponse()
            }
        )
        val productsCart = repo.getCartProducts(
            dukanId = dukanId,
            page = 0,
            size = 10
        )

        assertEquals(expected = demoPagedResultProductCart, actual = productsCart)
    }
}