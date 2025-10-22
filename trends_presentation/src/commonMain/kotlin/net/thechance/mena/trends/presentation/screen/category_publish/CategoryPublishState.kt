package net.thechance.mena.trends.presentation.screen.category_publish

import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal data class CategoryPublishState(
    val isLoading: Boolean = true,
    val error: CategoryPublishErrorState? = null,
    val categories: List<Selectable<CategoryUiState>> = emptyList(),
    val isPublishButtonLoadingVisible: Boolean = false
) {
    val isPublishButtonEnabled = categories.count { it.isSelected } in (1..3)
}

internal sealed class CategoryPublishErrorState {
    object NoInternet : CategoryPublishErrorState()
    data class RequestFailed(val message: String? = "Request failed") : CategoryPublishErrorState()
}
