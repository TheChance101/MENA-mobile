@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.repository

import net.thechance.mena.dukan.domain.entity.Order
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.repository.OrderRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderRepositoryImpl: OrderRepository {
    override suspend fun getOrderDetails(orderId: Uuid): Order {
        return createFakeOrder(orderId)
    }
}

fun createFakeOrder(id: Uuid): Order {
    return Order(
        id = id,
        orderNumber = 12345,
        orderDate = "2024-06-01",
        products = listOf(
            Order.ProductOrder(
                id = Uuid.random(),
                quantity = 2,
                imageUrl = "https://example.com/product1.jpg",
                name = "Product 1kfjgklgkljglkjgkjlglkjglkjglkjgkljgkljgkljglkjglkjglgjk",
                totalPrice = Price(20.0,30.0)
            ),
            Order.ProductOrder(
                id = Uuid.random(),
                quantity = 1,
                imageUrl = "https://example.com/product2.jpg",
                name = "Product 2",
                totalPrice = Price(20.0,30.0)
            ),
            Order.ProductOrder(
                id = Uuid.random(),
                quantity = 3,
                imageUrl = "https://example.com/product3.jpg",
                name = "Product 3",
                totalPrice = Price(20.0,30.0)
            ),
            Order.ProductOrder(
                id = Uuid.random(),
                quantity = 1,
                imageUrl = "https://example.com/product4.jpg",
                name = "Product 4",
                totalPrice = Price(20.0,30.0)
            ),
            Order.ProductOrder(
                id = Uuid.random(),
                quantity = 4,
                imageUrl = "https://example.com/product5.jpg",
                name = "Product 5",
                totalPrice = Price(20.0,30.0)
            ),
            Order.ProductOrder(
                id = Uuid.random(),
                quantity = 2,
                imageUrl = "https://example.com/product6.jpg",
                name = "Product 6",
                totalPrice = Price(20.0,30.0)
            )
        ),
        discount = 10.0,
        platformFees = 2.5,
        totalAmount = 100.0,
        orderAddress = Order.OrderAddress(
            addressDeliveryTitle = "123 Main St, Cityville",
            startLatitude = 37.7749,
            startLongitude = -122.4194,
            endLatitude = 37.7849,
            endLongitude = -122.4094
        ),
        customerName = "John Doe",
        customerPhone = "1234567890",
        isUserOwner = true
    )
}