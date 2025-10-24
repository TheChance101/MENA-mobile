package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager

@Composable
inline fun <T : Any> LazyRowItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    pager: Pager<Int, T>? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Theme.spacing._8),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Theme.spacing._16,
        vertical = Theme.spacing._8
    ),
    userScrollEnabled: Boolean = true,
    noinline key: ((T) -> Any)? = null,
    noinline contentType: (T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
    val lazyRowState = rememberLazyListState()
    pager?.let {
        lazyRowState.LoadMoreOnScroll(pager)
    }

    LazyRow(
        state = lazyRowState,
        modifier = modifier,
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
