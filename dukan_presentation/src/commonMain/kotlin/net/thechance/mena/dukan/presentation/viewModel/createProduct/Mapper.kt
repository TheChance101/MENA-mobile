package net.thechance.mena.dukan.presentation.viewModel.createProduct

import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.model.CreateProductParams
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Shelf.toUiState(): ShelfUiState {
    return ShelfUiState(
        id = id.toString(),
        name = name,
    )
}

fun ProductUiState.toCreateProductParam(shelfId: String): CreateProductParams {
    return CreateProductParams(
        name = productName,
        description = description,
        price = price.toDouble(),
        shelfId = shelfId
    )
}