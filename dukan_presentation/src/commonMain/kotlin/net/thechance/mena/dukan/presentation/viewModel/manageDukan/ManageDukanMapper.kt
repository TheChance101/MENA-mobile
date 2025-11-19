package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Shelf.toUiState(): ManageDukanUiState.ShelfUiState {
    return ManageDukanUiState.ShelfUiState(
        id = id.toString(),
        name = name
    )
}

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): ManageDukanUiState.ProductUiState {
    return ManageDukanUiState.ProductUiState(
        id = id.toString(),
        name = name,
        description = description,
        price = price.base,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        isOutOfStock = isOutOfStock
    )
}