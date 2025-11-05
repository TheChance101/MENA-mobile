package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan_cart.UpdateProductCartQuantityRequest
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams

fun UpdateProductCartQuantityParams.toDto(): UpdateProductCartQuantityRequest {
    return UpdateProductCartQuantityRequest(
        dukanId = dukanId,
        productId = productId,
        quantity = quantity
    )
}