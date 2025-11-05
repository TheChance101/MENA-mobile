package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.ProductCart
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ProductCart.toUiState(): CartItem {
    return CartItem(
        id = this.id.toString(),
        name = this.name,
        price = this.price,
        quantity = this.quantity
    )
}