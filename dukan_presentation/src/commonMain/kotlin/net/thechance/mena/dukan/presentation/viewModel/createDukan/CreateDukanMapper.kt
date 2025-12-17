package net.thechance.mena.dukan.presentation.viewModel.createDukan

import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Dukan.Coordinates
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@OptIn(ExperimentalUuidApi::class)
fun Color.toUiColor(): CreateDukanUiState.ColorUiState {
    return CreateDukanUiState.ColorUiState(
        id = id.toString(),
        color = hexCode.removePrefix("#").toLong(16) or 0xFF000000
    )
}

@OptIn(ExperimentalUuidApi::class)
fun CreateDukanUiState.ColorUiState.toEntity() = Color(
    id = Uuid.parse(id),
    hexCode = "#${color.toULong().toString(16).padStart(8, '0').uppercase()}"
)

@OptIn(ExperimentalUuidApi::class)
fun List<Category>.toUiState(): List<CreateDukanUiState.DukanCategoryUiState> {
    return map { category ->
        CreateDukanUiState.DukanCategoryUiState(
            id = category.id.toString(),
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
fun CreateDukanUiState.DukanCategoryUiState.toEntity() = Category(
    id = Uuid.parse(id),
    name = name,
    imageUrl = imageUrl
)

@OptIn(ExperimentalUuidApi::class)
fun CreateDukanUiState.toEntity() = Dukan(
    id = Uuid.random(),
    name = name,
    imageUrl = "",
    isFavorite = false,
    categories = selectedCategories.map { it.toEntity() }.toSet(),
    coordinates = currentLocation.toEntity(),
    address = address,
    status = Dukan.Status.PENDING,
    color = selectedColor?.toEntity() ?: Color(id = Uuid.random(), hexCode = ""),
    style = selectedStyle?.toEntityStyle() ?: Dukan.Style.WIDE_IMAGE
)

fun CreateDukanUiState.CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude,
)

fun CreateDukanUiState.CoordinatesUiState.toPosition() = Position(
    latitude = latitude,
    longitude = longitude
)

fun Position.toCoordinatesUiState() = CreateDukanUiState.CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)