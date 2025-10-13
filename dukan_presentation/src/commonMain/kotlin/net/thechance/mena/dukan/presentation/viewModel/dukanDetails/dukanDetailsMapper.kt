package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toUiColor

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


fun Shelf.toUiState() = DukanDetailsUiState.ShelfUiState(
    id = id,
    name = name
)


fun Product.toUiState() = DukanDetailsUiState.ProductUiState(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrls.firstOrNull().orEmpty()
)

