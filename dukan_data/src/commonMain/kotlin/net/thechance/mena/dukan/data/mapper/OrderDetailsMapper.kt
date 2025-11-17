@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.order.OrderDto
import net.thechance.mena.dukan.data.dto.order.OrderItemDto
import net.thechance.mena.dukan.domain.entity.Order
import net.thechance.mena.dukan.domain.entity.Price
import kotlin.uuid.ExperimentalUuidApi

fun OrderDto.toDomain(): Order {
    return Order(
        id = id,
        orderNumber = orderNumber.toInt(),
        orderDate = time,
        products = orderItemResponse.map { it.toDomain() },
        discount = discount,
        platformFees = platformFees,
        totalAmount = totalAmount,
        orderAddress = Order.OrderAddress(
            addressDeliveryTitle = addersLine,
            startLatitude = dukanLatitude,
            startLongitude = dukanLongitude,
            endLatitude = customerLatitude,
            endLongitude = customerLongitude
        ),
        customerName = customerName,
        customerPhone = customerNumber,
        isUserOwner = isDukanOwner
    )
}
private fun OrderItemDto.toDomain(): Order.ProductOrder {
    return Order.ProductOrder(
        id = productId,
        quantity = quantity,
        imageUrl = imageUrl.orEmpty(),
        name = productName,
        totalPrice = this.price.toDomain()
    )
}

private fun net.thechance.mena.dukan.data.dto.order.PriceDto.toDomain(): Price {
    return Price(
        base = this.original,
        final = this.finalPrice
    )
}