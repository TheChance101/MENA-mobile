package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
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

fun List<Category>.toUiState(): List<DukanCategoryUiState> {
    return map { category ->
        DukanCategoryUiState(
            id = category.id,
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}

fun CreateDukanUiState.CoordinatesUiState.toEntity() = Coordinates(
    latitude = latitude,
    longitude = longitude,
)