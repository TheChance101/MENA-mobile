@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import net.thechance.mena.dukan.domain.entity.Order
import kotlin.uuid.ExperimentalUuidApi

fun Order.toUiState(): OrderDetailsUiState.OrderUiState {
    return OrderDetailsUiState.OrderUiState(
        orderId = this.id,
        orderNumber = this.orderNumber,
        orderDate = this.orderDate,
        productInOrder = this.products.map { it.toUiState() },
        discount = this.discount,
        platformFees = this.platformFees,
        totalAmount = this.totalAmount,
        addressDeliveryUiState = this.orderAddress.toUiState(),
        customerName = this.customerName,
        customerPhone = this.customerPhone,
        isUserOwner = this.isUserOwner
    )
}

private fun Order.ProductOrder.toUiState(): OrderDetailsUiState.ProductInOrderUiState {
    return OrderDetailsUiState.ProductInOrderUiState(
        id = this.id,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
        name = this.name,
        totalPrice = this.totalPrice.final?:0.0
    )
}

private fun Order.OrderAddress.toUiState(): OrderDetailsUiState.AddressDeliveryUiState {
    return OrderDetailsUiState.AddressDeliveryUiState(
        addressDeliveryTitle = this.addressDeliveryTitle,
        startLatitude = this.startLatitude,
        startLongitude = this.startLongitude,
        endLatitude = this.endLatitude,
        endLongitude = this.endLongitude
    )
}