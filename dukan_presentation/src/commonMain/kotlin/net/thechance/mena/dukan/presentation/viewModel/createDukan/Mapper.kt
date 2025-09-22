package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Dukan.Coordinates
import net.thechance.mena.dukan.domain.entity.Dukan.Style

val defaultDukanStyles = Style.entries.map { style ->
    DukanStyleUiState(
        style = style,
        name = style.toUiStyleName()
    )
}

fun Style.toUiStyleName(): String = when (this) {
    Style.WIDE_IMAGE -> "Wide image with list products"
    Style.SMALL_IMAGE -> "Small image with grid products"
    Style.NO_IMAGE -> "No dukan image"
}

fun Color.toUiColor(): ColorUiState {
    return ColorUiState(
        id = id,
        color = hexCode.removePrefix("#").toLong(16) or 0xFF000000
    )
}

fun ColorUiState.toEntity() = Color(
    id = id,
    hexCode = "#${color.toULong().toString(16).padStart(8, '0').uppercase()}"
)

fun List<Category>.toUiState(): List<DukanCategoryUiState> {
    return map { category ->
        DukanCategoryUiState(
            id = category.id,
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}

fun DukanCategoryUiState.toEntity() = Category(
    id = id,
    name = name,
    imageUrl = imageUrl
)

fun CreateDukanUiState.toEntity() = Dukan(
    id = "",
    name = name,
    imageUrl = "",
    categories = selectedCategories.map { it.toEntity() }.toSet(),
    coordinates = currentLocation.toEntity(),
    address = address,
    status = Dukan.Status.PENDING,
    color = selectedColor?.toEntity()!!,
    style = selectedStyle!!
)

fun CreateDukanUiState.CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude,
)