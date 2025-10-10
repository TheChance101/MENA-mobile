package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import net.thechance.mena.dukan.domain.entity.Category

fun List<Category>.toCategoriesUiState(): List<CategoryUiState> =
    map { it.toUiState() }

private fun Category.toUiState(): CategoryUiState {
    return CategoryUiState(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}
