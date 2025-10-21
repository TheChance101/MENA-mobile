package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import net.thechance.mena.dukan.domain.entity.Category

fun Category.toUiState(): DukanCategoriesUiState.CategoryUiState {
    return DukanCategoriesUiState.CategoryUiState(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}