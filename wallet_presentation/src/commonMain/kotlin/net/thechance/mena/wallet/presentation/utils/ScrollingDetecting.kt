package net.thechance.mena.wallet.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
fun ScrollingDetecting(
    listSize: Int,
    endOfPages: Boolean,
    isLoading: Boolean,
    listState: LazyListState,
    buffer: Int = 5,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            lastVisibleItem.index >= listSize - buffer
                    && !endOfPages
                    && !isLoading
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }
}