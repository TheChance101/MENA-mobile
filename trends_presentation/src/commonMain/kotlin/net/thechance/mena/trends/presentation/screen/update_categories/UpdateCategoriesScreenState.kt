package net.thechance.mena.trends.presentation.screen.update_categories

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal data class UpdateCategoriesScreenState(
    val isLoading: Boolean = true,
    val errorState: ErrorState? = null,
    val initialCategories: List<Selectable<CategoryUiState>> = emptyList(),
    val categories: List<Selectable<CategoryUiState>> = emptyList(),
    val isSaveButtonLoading: Boolean = false
)

internal fun UpdateCategoriesScreenState.saveButtonEnabled() =
    categories.any(Selectable<CategoryUiState>::isSelected) && categories != initialCategories
