package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Dukan.Coordinates

fun Dukan.Style.toUiStyle(): CreateDukanUiState.Style = when (this) {
    Dukan.Style.WIDE_IMAGE -> CreateDukanUiState.Style.WIDE_IMAGE
    Dukan.Style.SMALL_IMAGE -> CreateDukanUiState.Style.SMALL_IMAGE
    Dukan.Style.NO_IMAGE -> CreateDukanUiState.Style.NO_IMAGE
}
fun Dukan.Style.toUiStyleName(): String = when (this) {
    Dukan.Style.WIDE_IMAGE -> "Wide image with list products"
    Dukan.Style.SMALL_IMAGE -> "Small image with grid products"
    Dukan.Style.NO_IMAGE -> "No dukan image"
}

fun CreateDukanUiState.Style.toEntityStyle(): Dukan.Style = when (this) {
    CreateDukanUiState.Style.WIDE_IMAGE -> Dukan.Style.WIDE_IMAGE
    CreateDukanUiState.Style.SMALL_IMAGE -> Dukan.Style.SMALL_IMAGE
    CreateDukanUiState.Style.NO_IMAGE -> Dukan.Style.NO_IMAGE
}

fun Color.toUiColor(): CreateDukanUiState.ColorUiState {
    return CreateDukanUiState.ColorUiState(
        id = id,
        color = hexCode.removePrefix("#").toLong(16) or 0xFF000000
    )
}

fun CreateDukanUiState.ColorUiState.toEntity() = Color(
    id = id,
    hexCode = "#${color.toULong().toString(16).padStart(8, '0').uppercase()}"
)

fun List<Category>.toUiState(): List<CreateDukanUiState.DukanCategoryUiState> {
    return map { category ->
        CreateDukanUiState.DukanCategoryUiState(
            id = category.id,
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}

fun CreateDukanUiState.DukanCategoryUiState.toEntity() = Category(
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
    color = selectedColor?.toEntity()?: Color(id = "", hexCode = ""),
    style = selectedStyle?.toEntityStyle()?: Dukan.Style.WIDE_IMAGE
)

fun CreateDukanUiState.CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude,
)