@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object PreviewOrderDetailsUiState {
    val orderDetailsUiState = OrderDetailsUiState(
        orderDetailsScreenState = OrderDetailsUiState.OrderDetailsScreenState.Success,
        orderUiState = OrderDetailsUiState.OrderUiState(
            orderId = Uuid.random(),
            orderNumber = 54123453,
            orderDate = "2024-06-15",
            productInOrder = listOf(
                OrderDetailsUiState.ProductInOrderUiState(
                    id = Uuid.random(),
                    quantity = 2,
                    imageUrl = "https://via.placeholder.com/150",
                    name = "Product 1",
                    totalPrice = 29.99
                ),
                OrderDetailsUiState.ProductInOrderUiState(
                    id = Uuid.random(),
                    quantity = 1,
                    imageUrl = "https://via.placeholder.com/150",
                    name = "Product 2",
                    totalPrice = 49.99
                )
            ),
            discount = 10.0,
            platformFees = 5.0,
            totalAmount = 74.98,
            addressDeliveryUiState = OrderDetailsUiState.AddressDeliveryUiState(
                addressDeliveryTitle = "123 Main St, City, Country",
                startLatitude = 37.7749,
                startLongitude = -122.4194,
                endLatitude = 37.7849,
                endLongitude = -122.4094
            ),
            customerName = "John Doe",
            customerPhone = "+12345687890"
        )
    )
}