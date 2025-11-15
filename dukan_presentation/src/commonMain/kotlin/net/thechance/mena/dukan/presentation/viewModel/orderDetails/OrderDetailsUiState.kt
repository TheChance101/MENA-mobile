@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class OrderDetailsUiState(
    val orderUiState: OrderUiState = OrderUiState(),
) {
    data class OrderUiState(
        val orderId: Uuid = Uuid.random(),
        val orderNumber: Int = 0,
        val orderDate: String = "",
        val productInOrder: List<ProductInOrderUiState> = emptyList(),
        val discount: Double = 0.0,
        val platformFees: Double = 0.0,
        val totalAmount: Double = 0.0,
        val addressDeliveryUiState: AddressDeliveryUiState = AddressDeliveryUiState(),
        val customerName: String = "",
        val customerPhone: String = "",
    )

    data class ProductInOrderUiState(
        val id: Uuid,
        val quantity: Int,
        val imageUrl: String,
        val name: String,
        val price: Double,
    )

    data class AddressDeliveryUiState(
        val addressDeliveryTitle: String = "",
        val startLatitude: Double = 0.0,
        val startLongitude: Double = 0.0,
        val endLatitude: Double = 0.0,
        val endLongitude: Double = 0.0
    )
}
