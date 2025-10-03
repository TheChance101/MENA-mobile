package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf

fun Shelf.toUiState(): ShelfUiState {
    return ShelfUiState(
        id = id,
        name = name
    )
}

fun Product.toUiState(): ProductUiState {
    return ProductUiState(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrls.firstOrNull().orEmpty()
    )
}