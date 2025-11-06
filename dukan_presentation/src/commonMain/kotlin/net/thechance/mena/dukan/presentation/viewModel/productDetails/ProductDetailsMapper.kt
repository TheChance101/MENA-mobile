package net.thechance.mena.dukan.presentation.viewModel.productDetails

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): ProductDetailsUiState.ProductInfo {
    return ProductDetailsUiState.ProductInfo(
        id = id.toString(),
        name = name,
        price = price,
        description = description,
        images = imageUrls,
        inCartQuantity = quantityInCart
    )
}

fun ProductDetailsUiState.ProductInfo.toDomainParams(dukanId: String): UpdateProductCartQuantityParams {
    return UpdateProductCartQuantityParams(
        productId = id,
        quantity = inCartQuantity,
        dukanId = dukanId
    )
}