package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState

@Composable
fun SelectionRow(
    availableItems: List<CreateDukanUiState.DukanCategoryUiState>,
    isItemSelected: (CreateDukanUiState.DukanCategoryUiState) -> Boolean,
    onItemClicked: (CreateDukanUiState.DukanCategoryUiState) -> Boolean,
    onItemEnabled: (CreateDukanUiState.DukanCategoryUiState) -> Boolean,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(availableItems) { item ->
            Chip(
                text = item.name,
                painter = rememberImagePainterOrNull(item.imageUrl),
                isSelected = isItemSelected(item),
                isEnabled = onItemEnabled(item),
                modifier = Modifier,
                iconSize = 16.dp,
                shape = RoundedCornerShape(Theme.radius.full),
                onClick = { onItemClicked(item) }
            )
        }
    }
}

@Composable
fun rememberImagePainterOrNull(url: String?): Painter? {
    return url?.takeIf { it.isNotEmpty() }?.let { rememberAsyncImagePainter(it) }
}
