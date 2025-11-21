@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import net.thechance.mena.dukan.domain.model.DukanPreview
import net.thechance.mena.dukan.domain.entity.ProductSearch
import kotlin.uuid.ExperimentalUuidApi


fun DukanPreview.toSearchUiState(): SearchUiState.DukanUiState{
    return SearchUiState.DukanUiState(
        id = id,
        title = name,
        imageUrl = imageUrl,
        isFavorite = isFavorite
    )
}

fun ProductSearch.toSearchUiState(): SearchUiState.ProductUiState{
    return SearchUiState.ProductUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        dukanName = dukanName,
        dukanId = dukanId,
        price = price,
        isOutOfStock = isOutOfStock
    )
}