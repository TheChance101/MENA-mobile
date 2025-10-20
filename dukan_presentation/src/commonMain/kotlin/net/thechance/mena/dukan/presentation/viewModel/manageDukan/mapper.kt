package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf

fun Shelf.toUiState(): ManageDukanUiState.ShelfUiState {
    return ManageDukanUiState.ShelfUiState(
        id = id,
        name = name
    )
}

fun Product.toUiState(): ManageDukanUiState.ProductUiState {
    return ManageDukanUiState.ProductUiState(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrls.firstOrNull().orEmpty()
    )
}