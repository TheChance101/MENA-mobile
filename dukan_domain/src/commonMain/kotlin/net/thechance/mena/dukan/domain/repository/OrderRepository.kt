@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Order
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface OrderRepository {
    suspend fun getOrderDetails(orderId: Uuid): Order
}