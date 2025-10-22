package net.thechance.mena.trends.presentation.screen.update_categories

import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal data class UpdateCategoriesScreenState(
    val isLoading: Boolean = true,
    val errorState: UpdateCategoryErrorState? = null,
    val initialCategories: List<Selectable<CategoryUiState>> = emptyList(),
    val categories: List<Selectable<CategoryUiState>> = emptyList(),
    val isSaveButtonLoading: Boolean = false
)

internal fun UpdateCategoriesScreenState.saveButtonEnabled() =
    categories.any(Selectable<CategoryUiState>::isSelected) && categories != initialCategories

internal sealed class UpdateCategoryErrorState {
    object NoInternet : UpdateCategoryErrorState()
    data class RequestFailed(val message: String? = "Request failed") : UpdateCategoryErrorState()
    object RequestTimeout: UpdateCategoryErrorState()
}