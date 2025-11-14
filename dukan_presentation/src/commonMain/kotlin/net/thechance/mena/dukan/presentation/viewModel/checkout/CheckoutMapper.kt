package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
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
    label = this?.addressType?.getAddressType() ?: "Unknown",
    street = this?.addressLine ?: "Unknown"
)