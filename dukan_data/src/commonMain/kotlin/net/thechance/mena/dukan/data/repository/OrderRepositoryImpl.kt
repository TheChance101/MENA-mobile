@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.thechance.mena.dukan.data.dto.order.OrderDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.util.constants.EndPoints.DUKAN_BASE_PATH
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Order
import net.thechance.mena.dukan.domain.repository.OrderRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderRepositoryImpl(
    private val client: HttpClient
): OrderRepository {
    override suspend fun getOrderDetails(orderId: Uuid): Order {
        return safeApiCall <OrderDto> {
            client.get(ORDER_DETAILS_ENDPOINT){
                parameter("orderId", orderId)
            }
        }.toDomain()
    }
    companion object {
        const val ORDER_DETAILS_ENDPOINT = "${DUKAN_BASE_PATH}/orders"
    }
}