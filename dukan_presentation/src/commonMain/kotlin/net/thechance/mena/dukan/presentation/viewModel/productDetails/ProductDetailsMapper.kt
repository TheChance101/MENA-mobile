package net.thechance.mena.dukan.presentation.viewModel.productDetails

import net.thechance.mena.dukan.domain.entity.Product
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): ProductDetailsUiState.ProductInfo {
    return ProductDetailsUiState.ProductInfo(
        id = id.toString(),
        name = name,
        price = price,
        description = description,
        images = imageUrls
    )
}