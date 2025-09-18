package net.thechance.mena.dukan.presentation.screen.createDukan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

@Composable
fun CategorySelectionRow(
    availableCategories: List<DukanCategoryUiState>,
    isCategorySelected: (DukanCategoryUiState) -> Boolean,
    onCategorySelected: (DukanCategoryUiState) -> Boolean,
    onCategoryDeselected: (DukanCategoryUiState) -> Boolean,
    onCategoryEnabled: (DukanCategoryUiState) -> Boolean
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        availableCategories.forEach { category ->
            item {
                CategoryChip(
                    category = category,
                    isSelected = isCategorySelected(category),
                    isEnabled = onCategoryEnabled(category),
                    onCategorySelected = onCategorySelected,
                    onCategoryDeselected = onCategoryDeselected
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: DukanCategoryUiState,
    isSelected: Boolean,
    isEnabled: Boolean,
    onCategorySelected: (DukanCategoryUiState) -> Boolean,
    onCategoryDeselected: (DukanCategoryUiState) -> Boolean
) {
    Chip(
        text = category.name,
        painter = rememberAsyncImagePainter(category.imageUrl),
        isSelected = isSelected,
        isEnabled = isEnabled,
        modifier = Modifier,
        iconSize = 16.dp,
        shape = RoundedCornerShape(Theme.radius.full),
        onClick = {
            if (isSelected) onCategoryDeselected(category) else onCategorySelected(category)
        }
    )
}