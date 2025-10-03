package net.thechance.mena.dukan.presentation.util.pagination

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun <T : Any> LazyListState.LoadMoreOnScroll(
    pager: Pager<Int, T>,
) {
    LaunchedEffect(pager , ) {
        snapshotFlow {
            val layoutInfo = layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex to totalItems
        }.distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (pager.isShouldLoadMore(lastVisible, total)) {
                    pager.load()
                }
            }
    }
}