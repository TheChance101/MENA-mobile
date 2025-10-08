package net.thechance.mena.dukan.presentation.screen.main.components.categorySection

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

fun calculateGridLayout(
    screenWidth: Dp,
    categories: List<DukanCategoryUiState>,
    rows: Int,
    itemWidth: Dp = 80.dp,
    spacing: Dp
): GridCalculation {
    val columnsCount = ((screenWidth + spacing) / (itemWidth + spacing))
        .toInt()
        .coerceAtLeast(2)

    val maxVisibleItems = columnsCount * rows
    val hasMoreItems = categories.size > maxVisibleItems - 1

    val itemsToShow = if (hasMoreItems) categories.take(maxVisibleItems - 1) else categories

    return GridCalculation(
        columnsCount = columnsCount,
        maxVisibleItems = maxVisibleItems,
        hasMoreItems = hasMoreItems,
        itemsToShow = itemsToShow
    )
}