package net.thechance.mena.dukan.presentation.viewModel.createProduct

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.util.CreateProductParams
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun Shelf.toUiState(): ShelfUiState {
    return ShelfUiState(
        id = id,
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