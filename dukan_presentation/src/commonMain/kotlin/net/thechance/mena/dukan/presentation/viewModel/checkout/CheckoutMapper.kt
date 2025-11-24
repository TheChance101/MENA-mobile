@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CheckoutParams
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.uuid.ExperimentalUuidApi

fun Product.toUiState(): CheckoutUiState.CartItem {
    return CheckoutUiState.CartItem(
        id = this.id.toString(),
        name = this.name,
        price = this.price.base,
        quantity = this.quantityInCart
    )
}

fun Cart.toUiState(): CheckoutUiState.CartDetails {
    return CheckoutUiState.CartDetails(
        discountPercentage = discount,
        totalPriceAfterDiscount = totalPriceAfterDiscount,
        totalPriceBeforeDiscount = totalPriceBeforeDiscount,
        platformFees = 1.99
    )
}

fun Address?.toUiState() = CheckoutUiState.Address(
    label = this?.addressType.let {
        when (it) {
            AddressType.Home -> CheckoutUiState.AddressLabel.Home
            AddressType.Office -> CheckoutUiState.AddressLabel.Office
            is AddressType.Other -> CheckoutUiState.AddressLabel.Other
            null -> CheckoutUiState.AddressLabel.Other
        }
    },
    street = this?.addressLine ?: "Unknown",
    latitude = this?.latitude ?: 0.0,
    longitude = this?.longitude ?: 0.0,
)

fun CheckoutUiState.toDomain(): CheckoutParams {
    return CheckoutParams(
        cartId = cartId,
        address = this.deliveryAddress.street,
        latitude = deliveryAddress.latitude,
        longitude = deliveryAddress.longitude
    )
}