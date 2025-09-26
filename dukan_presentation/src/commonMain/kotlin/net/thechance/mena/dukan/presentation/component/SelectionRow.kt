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

interface SelectableItem {
    val id: String
    val name: String
    val imageUrl: String
}

@Composable
fun <T : SelectableItem> SelectionRow(
    availableItems: List<T>,
    isItemSelected: (T) -> Boolean,
    onItemSelected: (T) -> Boolean,
    onItemDeselected: (T) -> Boolean,
    onItemEnabled: (T) -> Boolean
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
private fun <T : SelectableItem> SelectionChip(
    item: T,
    isSelected: Boolean,
    isEnabled: Boolean,
    onItemSelected: (T) -> Boolean,
    onItemDeselected: (T) -> Boolean
) {
    Chip(
        text = item.name,
        painter = if (item.imageUrl.isNotEmpty()) {
            rememberAsyncImagePainter(item.imageUrl)
        } else {
            null // No image for shelves
        },
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