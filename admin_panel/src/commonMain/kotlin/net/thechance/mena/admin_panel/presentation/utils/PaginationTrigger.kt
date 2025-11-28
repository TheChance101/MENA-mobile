package net.thechance.mena.admin_panel.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PaginationTrigger(
    listState: LazyListState,
    buffer: Int,
    loadNextItems: () -> Unit
) {
    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalCount = listState.layoutInfo.totalItemsCount
            lastVisible to totalCount
        }
            .distinctUntilChanged()
            .collect { (lastVisibleIndex, totalCount) ->
                lastVisibleIndex?.let {
                    val lastIndex = if (totalCount > 0) totalCount - 1 else -1
                    if (it >= lastIndex - buffer) {
                        loadNextItems()
                    }
                }
            }
    }
}