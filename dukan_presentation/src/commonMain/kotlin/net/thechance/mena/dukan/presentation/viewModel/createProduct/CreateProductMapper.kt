package net.thechance.mena.dukan.presentation.viewModel.createProduct

import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.model.CreateProductParams
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Shelf.toUiState(): CreateProductUiState.ShelfUiState {
    return CreateProductUiState.ShelfUiState(
        id = id.toString(),
        name = name,
    )
}

fun CreateProductUiState.toCreateProductParam(shelfId: String): CreateProductParams {
    return CreateProductParams(
        name = productName,
        description = description,
        price = Price(
            base = price.toDouble(),
            final = priceAfterDiscount.toDoubleOrNull()
        ),
        shelfId = shelfId
    )
}