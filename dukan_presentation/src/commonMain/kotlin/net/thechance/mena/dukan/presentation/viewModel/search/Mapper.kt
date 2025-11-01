@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.search

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import kotlin.uuid.ExperimentalUuidApi


fun Dukan.toSearchUiState(): SearchUiState.DukanUiState{
    return SearchUiState.DukanUiState(
        id = id,
        title = name,
        imageUrl = imageUrl,
        isFavorite = false // Todo (handle with is favorite story )
    )
}

fun Product.toSearchUiState(): SearchUiState.ProductUiState{
    return SearchUiState.ProductUiState(
        id = id,
        name = name,
        imageUrl = imageUrls.first(),
        dukanName = description,
        price = price
    )
}