package net.thechance.mena.trends.presentation.screen.category_pick

import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal fun List<Category>.toUiStates(): List<Selectable<CategoryUiState>> {
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