package net.thechance.mena.dukan.data.repository.mockEngine.dukan_cart

import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.dto.order.OrderDto
import net.thechance.mena.dukan.data.dto.order.OrderItemDto
import net.thechance.mena.dukan.data.dto.product.PriceDto
import net.thechance.mena.dukan.data.dto.product.ProductCartDto
import net.thechance.mena.dukan.data.mapper.toDomain
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
val cartDto = CartDto(
    id = Uuid.random(),
    totalPriceBeforeDiscount = 150.0,
    totalPriceAfterDiscount = 100.0,
    discount = 50.0
)

val cart1 = cartDto.toDomain()

@OptIn(ExperimentalUuidApi::class)
val productCartDto1 = ProductCartDto(
    id = Uuid.random(),
    name = "Demo Product 1",
   price = PriceDto(
       base = 100.0,
       final = 100.0
   ),
    description = "This is a demo product",
    imageUrl = "https://picsum.photos/200/200?random=1",
    quantityInCart = 10
)

@OptIn(ExperimentalUuidApi::class)
val productCartDto2 = ProductCartDto(
    id = Uuid.random(),
    name = "Demo Product 2",
    price = PriceDto(
        base = 10.0,
        final = 10.0
    ),
    description = "This is a demo product",
    imageUrl = "https://picsum.photos/200/200?random=1",
    quantityInCart = 10
)


val demoPagedResultProductCart: PagedResult<Product> = PageResponseDto(
    content = listOf(productCartDto1, productCartDto2),
    number = 0,
    size = 2,
    totalPages = 1,
    totalElements = 2,
    first = true,
    last = true
).toDomain { it.toDomain() }

@OptIn(ExperimentalUuidApi::class)
val cartOdrerDto = OrderDto(
    id = Uuid.random(),
    orderNumber = 10,
    time = "",
    orderItemResponse = listOf(
        OrderItemDto(
            productId = Uuid.random(),
            productName = "Demo Product 1",
            imageUrl = "https://picsum.photos/200/200?random=1",
            quantity = 10,
            price = net.thechance.mena.dukan.data.dto.order.PriceDto(
                original = 100.0,
                finalPrice = 100.0
            )
        )
    ),
    discount = 50.2,
    platformFees = 60.4,
    totalAmount = 20.8,
    addersLine = "",
    customerLatitude = 2524.2,
    customerLongitude = 2452.5,
    dukanLatitude = 50.25,
    dukanLongitude = 2581.4,
    customerName = "",
    customerNumber = "",
    customerImage = "dukan image.png",
    isDukanOwner = true
)