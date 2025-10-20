package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Dukan.Coordinates
import net.thechance.mena.dukan.domain.entity.Dukan.Style
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@OptIn(ExperimentalUuidApi::class)
fun Color.toUiColor(): ColorUiState {
    return ColorUiState(
        id = id.toString(),
        color = hexCode.removePrefix("#").toLong(16) or 0xFF000000
    )
}

@OptIn(ExperimentalUuidApi::class)
fun ColorUiState.toEntity() = Color(
    id = Uuid.random(),
    hexCode = "#${color.toULong().toString(16).padStart(8, '0').uppercase()}"
)

@OptIn(ExperimentalUuidApi::class)
fun List<Category>.toUiState(): List<DukanCategoryUiState> {
    return map { category ->
        DukanCategoryUiState(
            id = category.id.toString(),
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
fun DukanCategoryUiState.toEntity() = Category(
    id = Uuid.random(),
    name = name,
    imageUrl = imageUrl
)

@OptIn(ExperimentalUuidApi::class)
fun CreateDukanUiState.toEntity() = Dukan(
    id = Uuid.random(),
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