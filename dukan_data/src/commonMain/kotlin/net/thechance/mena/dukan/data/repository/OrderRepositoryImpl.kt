@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.repository

import io.ktor.client.request.get
import net.thechance.mena.dukan.data.dto.order.OrderDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.data.util.constants.EndPoints.DUKAN_BASE_PATH
import net.thechance.mena.dukan.data.util.network.DukanApi
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Order
import net.thechance.mena.dukan.domain.repository.OrderRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderRepositoryImpl(
    private val client: DukanApi
): OrderRepository {
    override suspend fun getOrderDetails(orderId: Uuid): Order {
        return safeApiCall <OrderDto> {
            client.getClient().get("${DUKAN_BASE_PATH}/orders/$orderId")
        }.toDomain()
    }
}