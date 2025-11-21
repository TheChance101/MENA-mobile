@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import net.thechance.mena.dukan.domain.entity.Order
import net.thechance.mena.dukan.domain.entity.Price
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object OrderDetailsTest {
    fun createFakeOrderDetails(orderId: Uuid): Order {
        return Order(
            id = orderId,
            orderDate = "2024-06-15",
            products = listOf(
                Order.ProductOrder(
                    id = Uuid.random(),
                    quantity = 2,
                    imageUrl = "https://via.placeholder.com/150",
                    name = "Product 1",
                    totalPrice = Price(base = 10.0, final = 29.99)
                ),
                Order.ProductOrder(
                    id = Uuid.random(),
                    quantity = 1,
                    imageUrl = "https://via.placeholder.com/150",
                    name = "Product 2",
                    totalPrice = Price(base = 20.0, final = 39.99)
                )
            ),
            discount = 10.0,
            platformFees = 5.0,
            totalAmount = 74.98,
            orderAddress = Order.OrderAddress(
                addressDeliveryTitle = "123 Main St, City, Country",
                startLatitude = 37.7749,
                startLongitude = -122.4194,
                endLatitude = 37.7849,
                endLongitude = -122.4094
            ),
            customerName = "John Doe",
            customerPhone = "+12345687890",
            isUserOwner = true,
            orderNumber = 100
        )
    }
}