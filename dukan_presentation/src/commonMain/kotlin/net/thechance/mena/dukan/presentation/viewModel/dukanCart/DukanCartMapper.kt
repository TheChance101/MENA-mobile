package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState() = DukanCartUiState.ProductUiState(
    id = id.toString(),
    name = name,
    description = description,
    price = price.base,
    imageUrl = imageUrls.firstOrNull().orEmpty(),
    quantity = quantityInCart,
    isOutOfStock = isOutOfStock
)

@OptIn(ExperimentalUuidApi::class)
fun Dukan.toUiState() = DukanCartUiState.DukanInfoUiState(
    id = id.toString(),
    name = name,
    imageUrl = imageUrl
)