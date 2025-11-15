@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.domain.entity

import net.thechance.mena.dukan.domain.model.OrderAddress
import net.thechance.mena.dukan.domain.model.ProductOrder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Order(
    val id: Uuid,
    val orderNumber: Int,
    val orderDate: String,
    val products: List<ProductOrder>,
    val discount: Double,
    val platformFee: Double,
    val totalAmount: Double,
    val orderAddress: OrderAddress,
    val customerName: String,
    val customerPhone: String,
)
