package net.thechance.mena.trends.presentation.shared.model.mapper

import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal fun List<Category>.toUserCategoryUiState(): List<Selectable<CategoryUiState>> {
    return map { category ->
        Selectable(
            value = category.toUiState(),
            isSelected = category.isSelected
        )
    }
}

internal fun List<Category>.toTrendCategoryUiState(): List<Selectable<CategoryUiState>> {
    return map { category ->
        Selectable(
            value = category.toUiState(),
            isSelected = false
        )
    }
}

private fun Category.toUiState(): CategoryUiState {
    return CategoryUiState(
        id = id,
        name = name,
        emoji = emoji,
    )
}