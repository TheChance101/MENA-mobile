package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Shelf.toUiState(): ShelfUiState {
    return ShelfUiState(
        id = id.toString(),
        name = name
    )
}

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): ProductUiState {
    return ProductUiState(
        id = id.toString(),
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrls.firstOrNull().orEmpty()
    )
}