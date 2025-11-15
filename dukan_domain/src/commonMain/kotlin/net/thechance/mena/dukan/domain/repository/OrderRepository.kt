package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Order

interface OrderRepository {
    fun getOrderDetails(orderId: String): Order
}