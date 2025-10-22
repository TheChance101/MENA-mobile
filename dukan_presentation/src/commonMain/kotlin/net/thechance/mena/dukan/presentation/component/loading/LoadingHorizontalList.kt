package net.thechance.mena.dukan.presentation.component.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
inline fun LoadingHorizontalList(
    itemCount: Int = 8,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Theme.spacing._16,
        vertical = Theme.spacing._8
    ),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Theme.spacing._8),
    crossinline itemContent: @Composable () -> Unit,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing._8),
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
    ) {
        items(
            itemCount,
        ) { itemContent() }
    }
}