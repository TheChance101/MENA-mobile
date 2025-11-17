@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Order(
    val id: Uuid,
    val orderNumber: Int,
    val orderDate: String,
    val products: List<ProductOrder>,
    val discount: Double,
    val platformFees: Double,
    val totalAmount: Double,
    val orderAddress: OrderAddress,
    val customerName: String,
    val customerPhone: String,
    val isUserOwner: Boolean
){
    data class OrderAddress(
        val addressDeliveryTitle: String,
        val startLatitude: Double,
        val startLongitude: Double,
        val endLatitude: Double,
        val endLongitude: Double
    )
    data class ProductOrder(
        val id: Uuid,
        val quantity: Int,
        val imageUrl:String,
        val name: String,
        val totalPrice: Price
    )
}
