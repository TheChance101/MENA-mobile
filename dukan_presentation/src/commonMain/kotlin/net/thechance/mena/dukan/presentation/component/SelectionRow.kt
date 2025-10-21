package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState

@Composable
fun SelectionRow(
    categories: List<CreateDukanUiState.DukanCategoryUiState>,
    isItemSelected: (CreateDukanUiState.DukanCategoryUiState) -> Boolean,
    onItemClick: (CreateDukanUiState.DukanCategoryUiState) -> Unit,
    isItemEnabled: (CreateDukanUiState.DukanCategoryUiState) -> Boolean
) {
    LazyRowItems(
        items = categories,
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
    ) { category ->
        Chip(
            text = category.name,
            painter = category.imageUrl.takeIf { it.isNotEmpty() }
                ?.let { rememberAsyncImagePainter(it) },
            isSelected = isItemSelected(category),
            isEnabled = isItemEnabled(category),
            iconSize = 16.dp,
            shape = RoundedCornerShape(Theme.radius.full),
            onClick = { onItemClick(category) }
        )
    }
}