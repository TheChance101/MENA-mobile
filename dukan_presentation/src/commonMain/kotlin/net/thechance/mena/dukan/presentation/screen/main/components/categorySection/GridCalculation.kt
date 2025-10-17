package net.thechance.mena.dukan.presentation.screen.main.components.categorySection

import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

data class GridCalculation(
    val columnsCount: Int,
    val maxVisibleItems: Int,
    val hasMoreItems: Boolean,
    val itemsToShow: List<DukanCategoryUiState>
)