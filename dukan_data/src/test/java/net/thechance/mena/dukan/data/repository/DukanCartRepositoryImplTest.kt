package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultUpdateProductQuantityRepository
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultUpdateProductQuantityResponse
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import org.junit.Test
import kotlin.test.assertTrue

class DukanCartRepositoryImplTest {

    @Test
    fun `Successful product quantity update`()= runTest{
        var called = false
        val repo=defaultUpdateProductQuantityRepository(
            dukanCartResponse = {
                called = true
                defaultUpdateProductQuantityResponse()
            }
        )

        repo.updateProductQuantity(
            params = UpdateProductCartQuantityParams(
                dukanId = "10",
                productId = "1",
                quantity = 5
            )
        )
        assertTrue(called)

    }
}