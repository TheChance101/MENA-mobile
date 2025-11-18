package net.thechance.mena.dukan.presentation.screen.main.components.categorySection

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState

fun calculateGridLayout(
    screenWidth: Dp,
    categories: List<DukanCategoryUiState>,
    rows: Int,
    itemWidth: Dp = 80.dp,
    itemHeight: Dp = 80.dp,
): GridCalculation {
    val columnsCount = (screenWidth / itemWidth).toInt().coerceAtLeast(2)
    val maxVisibleItems = columnsCount * rows
    val hasMoreItems = categories.size > maxVisibleItems - 1
    val visibleItems = if (hasMoreItems) categories.take(maxVisibleItems - 1) else categories

    val rowHeight = itemHeight * rows

    return GridCalculation(
        columnsCount = columnsCount,
        visibleItems = visibleItems,
        hasMoreItems = hasMoreItems,
        rowHeight = rowHeight
    )
}