package net.thechance.mena.dukan.data.repository

import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.defaultCartOrdersResponse
import net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart.dukanCartOrdersRepository
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class OrderRepositoryImplTest {


    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `test get orders`() = runTest {
        var called = false
        val orderId = Uuid.random()
        val repo = dukanCartOrdersRepository(
            orerId = orderId ,
            cartOrdersResponse = {
                called = true
                defaultCartOrdersResponse()
            }
        )
        repo.getOrderDetails(orderId = orderId)
        assertTrue(called)
    }

}