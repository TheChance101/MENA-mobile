package net.thechance.mena.trends.presentation.screen.category_pick

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal data class CategoryPickScreenState(
    val isLoading: Boolean = true,
    val error: ErrorState? = null,
    val categories: List<Selectable<CategoryUiState>> = emptyList(),
    val isNextButtonLoading: Boolean = false
)

internal fun CategoryPickScreenState.isNextButtonEnabled() = categories.any(Selectable<CategoryUiState>::isSelected)