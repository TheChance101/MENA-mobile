package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Dukan

fun List<Category>.toUiState(): List<DukanCategoryUiState> {
    return map { category ->
        DukanCategoryUiState(
            id = category.id,
            name = category.name,
            imageUrl = category.imageUrl
        )
    }
}

fun CreateDukanUiState.CoordinatesUiState.toEntity() = Dukan.Coordinates(
    latitude = latitude,
    longitude = longitude,
)