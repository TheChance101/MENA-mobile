@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.CheckoutParams
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
import kotlin.uuid.ExperimentalUuidApi

fun Product.toUiState(): CheckoutUiState.CartItem {
    return CheckoutUiState.CartItem(
        id = this.id.toString(),
        name = this.name,
        price = this.price.base,
        quantity = this.quantityInCart
    )
}

fun Address?.toUiState() = CheckoutUiState.Address(
    label = this?.addressType?.getAddressType() ?: "Unknown",
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