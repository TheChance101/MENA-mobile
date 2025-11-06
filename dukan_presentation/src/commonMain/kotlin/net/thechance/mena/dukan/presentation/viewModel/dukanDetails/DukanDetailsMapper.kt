package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toUiColor
import kotlin.uuid.ExperimentalUuidApi

fun Dukan.toUiState() = DukanDetailsUiState.DukanInfo(
    name = name,
    imageUrl = imageUrl,
    coordinates = DukanDetailsUiState.Coordinates(
        latitude = coordinates.latitude,
        longitude = coordinates.longitude
    ),
    color = color.toUiColor().color,
    style = DukanDetailsUiState.Style.valueOf(style.name)
)


@OptIn(ExperimentalUuidApi::class)
fun Shelf.toUiState() = DukanDetailsUiState.ShelfUiState(
    id = id.toString(),
    name = name
)


@OptIn(ExperimentalUuidApi::class)
fun Product.toUiState() = DukanDetailsUiState.ProductUiState(
    id = id.toString(),
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrls.firstOrNull().orEmpty(),
    inCartQuantity = quantityInCart,
)

fun DukanDetailsUiState.ProductUiState.toDomainParams(dukanId: String): UpdateProductCartQuantityParams {
    return UpdateProductCartQuantityParams(
        productId = id,
        quantity = inCartQuantity,
        dukanId = dukanId
    )
}

