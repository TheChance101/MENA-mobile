package net.thechance.mena.dukan.presentation.viewModel.productDetails

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState(): ProductDetailsUiState.ProductInfo {
    return ProductDetailsUiState.ProductInfo(
        id = id.toString(),
        name = name,
        basePrice = price.base,
        finalPrice = price.final ?: price.base,
        description = description,
        images = imageUrls,
        inCartQuantity = quantityInCart,
        isOutOfStock = isOutOfStock,
        finalProductQuantity = quantityInCart
    )
}

fun ProductDetailsUiState.ProductInfo.toDomainParams(dukanId: String): UpdateProductCartQuantityParams {
    return UpdateProductCartQuantityParams(
        productId = id,
        quantity = inCartQuantity,
        dukanId = dukanId
    )
}

fun parseHexColor(color: String): Long = color.removePrefix("#").toLong(16) or 0xFF000000