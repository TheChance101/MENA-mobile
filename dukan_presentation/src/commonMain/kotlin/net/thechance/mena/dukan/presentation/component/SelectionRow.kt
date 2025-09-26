package net.thechance.mena.dukan.presentation.component

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
fun SelectionRow(
    availableItems: List<DukanCategoryUiState>,
    isItemSelected: (DukanCategoryUiState) -> Boolean,
    onItemSelected: (DukanCategoryUiState) -> Boolean,
    onItemDeselected: (DukanCategoryUiState) -> Boolean,
    onItemEnabled: (DukanCategoryUiState) -> Boolean
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        availableItems.forEach { item ->
            item {
                SelectionChip(
                    item = item,
                    isSelected = isItemSelected(item),
                    isEnabled = onItemEnabled(item),
                    onItemSelected = onItemSelected,
                    onItemDeselected = onItemDeselected
                )
            }
        }
    }
}

@Composable
private fun SelectionChip(
    item: DukanCategoryUiState,
    isSelected: Boolean,
    isEnabled: Boolean,
    onItemSelected: (DukanCategoryUiState) -> Boolean,
    onItemDeselected: (DukanCategoryUiState) -> Boolean
) {
    Chip(
        text = item.name,
        painter = rememberAsyncImagePainter(item.imageUrl),
        isSelected = isSelected,
        isEnabled = isEnabled,
        modifier = Modifier,
        iconSize = 16.dp,
        shape = RoundedCornerShape(Theme.radius.full),
        onClick = {
            if (isSelected) onItemDeselected(item) else onItemSelected(item)
        }
    )
}
