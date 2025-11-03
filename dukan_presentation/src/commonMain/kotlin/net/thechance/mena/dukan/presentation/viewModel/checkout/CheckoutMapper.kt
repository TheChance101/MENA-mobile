package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.ProductCart

fun ProductCart.toUiState(): CartItem {
    return CartItem(
        name = this.name,
        price = this.price,
        quantity = this.quantity
    )
}