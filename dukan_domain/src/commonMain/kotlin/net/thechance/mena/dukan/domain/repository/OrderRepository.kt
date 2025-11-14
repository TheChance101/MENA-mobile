package net.thechance.mena.dukan.domain.repository

interface OrderRepository {
    fun getOrderDetails(orderId: String)
}