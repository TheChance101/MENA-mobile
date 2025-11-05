package net.thechance.mena.dukan.presentation.viewModel.editProduct

import net.thechance.mena.dukan.domain.entity.Shelf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Shelf.toUiState(): EditProductUiState.ShelfUiState {
    return EditProductUiState.ShelfUiState(
        id = id.toString(),
        name = name,
    )
}

