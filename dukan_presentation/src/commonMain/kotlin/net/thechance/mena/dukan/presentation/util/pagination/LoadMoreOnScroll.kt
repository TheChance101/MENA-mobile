package net.thechance.mena.dukan.presentation.util.pagination

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun <T : Any> LazyListState.LoadMoreOnScroll(
    pagerOld: PagerOld<Int, T>,
) {
    LaunchedEffect(pagerOld) {
        snapshotFlow {
            val layoutInfo = layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex to totalItems
        }.distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (pagerOld.isShouldLoadMore(lastVisible, total)) {
                    pagerOld.load()
                }
            }
    }
}

@Composable
fun <T : Any> LazyGridState.LoadMoreOnScroll(
    pagerOld: PagerOld<Int, T>,
) {
    LaunchedEffect(pagerOld) {
        snapshotFlow {
            val layoutInfo = layoutInfo

            val totalItems = layoutInfo.totalItemsCount

            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex to totalItems

        }.distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (pagerOld.isShouldLoadMore(lastVisible, total)) {
                    pagerOld.load()
                }
            }
    }
}