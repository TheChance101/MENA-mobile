package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState() = ShelfDetailsUiState.ProductUiState(
    id = id.toString(),
    name = name,
    description = description,
    price = price.base,
    imageUrl = imageUrls.firstOrNull().orEmpty(),
    inCartQuantity = quantityInCart,
    isOutOfStock = isOutOfStock
)

fun ShelfDetailsUiState.ProductUiState.toDomainParams(dukanId: String): UpdateProductCartQuantityParams {
    return UpdateProductCartQuantityParams(
        productId = id,
        quantity = inCartQuantity,
        dukanId = dukanId
    )
}

fun Dukan.Style.toShelfStyle(): ShelfDetailsUiState.Style {
    return when (this) {
        Dukan.Style.NO_IMAGE -> ShelfDetailsUiState.Style.NO_IMAGE
        Dukan.Style.SMALL_IMAGE -> ShelfDetailsUiState.Style.SMALL_IMAGE
        Dukan.Style.WIDE_IMAGE -> ShelfDetailsUiState.Style.WIDE_IMAGE
    }
}

@OptIn(ExperimentalUuidApi::class)
fun Color.toUiColor(): CreateDukanUiState.ColorUiState {
    return CreateDukanUiState.ColorUiState(
        id = id.toString(),
        color = hexCode.removePrefix("#").toLong(16) or 0xFF000000
    )
}
