package net.thechance.mena.dukan.presentation.viewModel.createProduct

import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.util.CreateProductParams

fun Shelf.toUiState(): CreateProductUiState.ShelfUiState {
    return CreateProductUiState.ShelfUiState(
        id = id,
        name = name,
    )
}

fun CreateProductUiState.toCreateProductParam(shelfId: String): CreateProductParams {
    return CreateProductParams(
        name = productName,
        description = description,
        price = price.toDouble(),
        shelfId = shelfId
    )
}