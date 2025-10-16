package net.thechance.mena.trends.presentation.screen.update_interests

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.CategoryUiState
import net.thechance.mena.trends.presentation.shared.model.Selectable

internal data class UpdateInterestsScreenState(
    val isLoading: Boolean = true,
    val error: ErrorState? = null,
    val categories: List<Selectable<CategoryUiState>> = emptyList(),
    val isNextButtonLoading: Boolean = false
)

internal fun UpdateInterestsScreenState.isNextButtonEnabled() =
    categories.any(Selectable<CategoryUiState>::isSelected)