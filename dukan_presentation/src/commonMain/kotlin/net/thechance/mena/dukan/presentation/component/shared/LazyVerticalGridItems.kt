package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager

@Composable
inline fun <T : Any> LazyVerticalGridItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    grid: GridCells = GridCells.Fixed(1),
    pager: Pager<Int, T>? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Theme.spacing._8),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Theme.spacing._8),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Theme.spacing._16,
        vertical = Theme.spacing._12
    ),
    userScrollEnabled: Boolean = true,
    noinline key: ((T) -> Any)? = null,
    noinline contentType: (T) -> Any? = { null },
    crossinline itemContent: @Composable LazyGridItemScope.(item: T) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    pager?.let {
        lazyGridState.LoadMoreOnScroll(pager)
    }

    LazyVerticalGrid(
        columns = grid,
        state = lazyGridState,
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        userScrollEnabled = userScrollEnabled
    ) {
        items(
            items = items,
            key = key,
            contentType = contentType
        ) { item ->
            itemContent(item)
        }
    }
}
