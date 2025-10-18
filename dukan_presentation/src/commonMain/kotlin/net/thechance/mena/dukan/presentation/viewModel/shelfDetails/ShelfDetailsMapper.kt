package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import net.thechance.mena.dukan.domain.entity.Product

fun Product.toUiState() = ShelfDetailsUiState.ProductUiState(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrls.firstOrNull().orEmpty()
)