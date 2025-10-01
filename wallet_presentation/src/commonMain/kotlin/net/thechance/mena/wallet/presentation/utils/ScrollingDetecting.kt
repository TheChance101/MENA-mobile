package net.thechance.mena.wallet.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState

@Composable
fun ScrollingDetecting(
    state: TransactionHistoryScreenState,
    listState: LazyListState,
    buffer: Int = 5,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            lastVisibleItem.index >= state.history.size - buffer
                    && !state.endOfPages
                    && !state.isPaginationLoading
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }
}