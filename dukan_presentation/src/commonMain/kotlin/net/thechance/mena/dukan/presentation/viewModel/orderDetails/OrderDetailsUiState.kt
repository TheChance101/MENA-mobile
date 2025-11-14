@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class OrderDetailsUiState(
    val orderUiState: OrderUiState = OrderUiState(),
) {
    data class OrderUiState(
        val orderId: Uuid = Uuid.random(),
        val orderDate: String = "",
        val productInOrder: List<ProductInOrderUiState> = emptyList(),
        val discount: Double = 0.0,
        val platformFees: Double = 0.0,
        val totalAmount: Double = 0.0,
        val addressDelivered: String = "",
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
}
