@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.cart.CheckoutRequest
import net.thechance.mena.dukan.domain.model.CheckoutParams
import kotlin.uuid.ExperimentalUuidApi

fun CheckoutParams.toDto(): CheckoutRequest {
    return CheckoutRequest(
        cartId = cartId,
        address = address,
        longitude = longitude,
        latitude = latitude
    )
}