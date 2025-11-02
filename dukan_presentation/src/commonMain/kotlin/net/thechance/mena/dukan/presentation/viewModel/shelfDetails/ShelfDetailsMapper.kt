package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState() = ShelfDetailsUiState.ProductUiState(
    id = id.toString(),
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrls.firstOrNull().orEmpty(),
    inCartQuantity = quantityInCart
)
