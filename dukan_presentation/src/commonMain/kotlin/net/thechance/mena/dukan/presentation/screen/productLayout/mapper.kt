package net.thechance.mena.dukan.presentation.screen.productLayout

import net.thechance.mena.dukan.domain.entity.Product

fun Product.toUiState(): ProductUiState {
    return ProductUiState(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrls.firstOrNull().orEmpty()
    )
}