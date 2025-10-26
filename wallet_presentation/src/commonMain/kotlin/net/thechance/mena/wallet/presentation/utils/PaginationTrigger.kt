package net.thechance.mena.wallet.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun <T> PaginationTrigger(
    list: List<T>,
    listState: LazyListState,
    buffer: Int,
    loadNextItems: () -> Unit
) {
    LaunchedEffect(list) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                lastVisibleIndex?.let {lastVisibleIndex ->
                    if (lastVisibleIndex >= list.lastIndex - buffer) {
                        loadNextItems()
                    }
                }
            }
    }
}