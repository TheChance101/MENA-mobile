package net.thechance.mena.dukan.presentation.viewModel.checkout

import net.thechance.mena.dukan.domain.entity.Product
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): CartItem {
    return CartItem(
        id = this.id.toString(),
        name = this.name,
        price = this.price,
        quantity = this.quantityInCart
    )
}