package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.cart.CartDto
import net.thechance.mena.dukan.data.dto.cart.UpdateProductCartQuantityRequest
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import kotlin.uuid.ExperimentalUuidApi

fun UpdateProductCartQuantityParams.toDto(): UpdateProductCartQuantityRequest {
    return UpdateProductCartQuantityRequest(
        dukanId = dukanId,
        productId = productId,
        quantity = quantity
    )
}

@OptIn(ExperimentalUuidApi::class)
fun CartDto.toDomain() = Cart(
    id = id,
    totalPriceBeforeDiscount = totalPriceBeforeDiscount,
    totalPriceAfterDiscount = totalPriceAfterDiscount,
    discount = discount
)