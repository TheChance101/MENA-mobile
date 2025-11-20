package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): CheckoutUiState.CartItem {
    return CheckoutUiState.CartItem(
        id = this.id.toString(),
        name = this.name,
        price = this.price.base,
        quantity = this.quantityInCart
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
    street = this?.addressLine ?: "Unknown"
)