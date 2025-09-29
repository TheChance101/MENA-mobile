package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun <T> SelectionRow(
    availableItems: List<T>,
    isItemSelected: (T) -> Boolean,
    onItemSelected: (T) -> Boolean,
    onItemDeselected: (T) -> Boolean,
    onItemEnabled: (T) -> Boolean,
    getItemName: (T) -> String,
    getItemImageUrl: (T) -> String = { "" }
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            items = availableItems,
            key = { item -> getItemName(item) }
        ) { item ->
            SelectionChip(
                item = item,
                isSelected = isItemSelected(item),
                isEnabled = onItemEnabled(item),
                onItemSelected = onItemSelected,
                onItemDeselected = onItemDeselected,
                getItemName = getItemName,
                getItemImageUrl = getItemImageUrl
            )
        }
    }
}

@Composable
private fun <T> SelectionChip(
    item: T,
    isSelected: Boolean,
    isEnabled: Boolean,
    onItemSelected: (T) -> Boolean,
    onItemDeselected: (T) -> Boolean,
    getItemName: (T) -> String,
    getItemImageUrl: (T) -> String
) {
    Chip(
        text = getItemName(item),
        painter = if (getItemImageUrl(item).isNotEmpty())
            rememberAsyncImagePainter(getItemImageUrl(item)) else null,
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