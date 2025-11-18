package net.thechance.mena.dukan.presentation.screen.main.components.categorySection

import androidx.compose.ui.unit.Dp
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState

data class GridCalculation(
    val columnsCount: Int,
    val visibleItems: List<CreateDukanUiState.DukanCategoryUiState>,
    val hasMoreItems: Boolean,
    val rowHeight: Dp
)