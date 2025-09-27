package net.thechance.mena.dukan.presentation.component.pagination

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LazyListState.LoadMoreOnScroll(
    hasMore: Boolean,
    isLoading: Boolean,
    loadNextPage: suspend () -> Unit,
    buffer: Int = 0
) {
    LaunchedEffect(Unit) {
        snapshotFlow {
            val layoutInfo = this@LoadMoreOnScroll.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= totalItems - buffer - 1
        }.distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && hasMore && !isLoading) {
                    loadNextPage()
                }
            }
    }
}
