package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState

@Composable
fun NoImageDukanShelves(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>
) {
    val lazyRowListState = rememberLazyListState()
    lazyRowListState.LoadMoreOnScroll(pagerShelves)

    val lazyColumnListState = rememberLazyListState()
    lazyColumnListState.LoadMoreOnScroll(pagerShelves)

    val coroutineScope = rememberCoroutineScope()
    var chipsAlpha by rememberSaveable { mutableStateOf(0f) }
    val layoutInfo by remember {
        derivedStateOf { lazyColumnListState.layoutInfo }
    }

    LaunchedEffect(layoutInfo) {
        synchronizeScrollsAndAlpha(
            lazyColumnListState.firstVisibleItemIndex,
            layoutInfo,
            state,
            listener,
            coroutineScope,
            lazyRowListState
        ) { alpha -> chipsAlpha = alpha }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = Theme.spacing._8),
        state = lazyColumnListState
    ) {
        item(key = "BestSelling") {
            BestSellingNoImageDukan(
                state = state,
                listener = listener
            )
        }
        stickyHeader(key = "ShelvesChips") {
            NoImageDukanShelvesChips(
                state = state,
                lazyRowState = lazyRowListState,
                onClick = { shelfId, index ->
                    listener.onShelfClicked(shelfId)
                    coroutineScope.launch {
                        lazyColumnListState.animateScrollToItem(index + SHELVES_OFFSET)
                    }
                },
                alpha = chipsAlpha
            )
        }
        items(count = state.shelves.items.size, key = { state.shelves.items[it].id }) {
            val shelf = state.shelves.items[it]
            NoImageDukanShelfWithProducts(
                shelf = shelf,
                listener = listener,
                dukanColor = state.dukanInfo.color
            )
        }
    }
}

private const val SHELVES_OFFSET = 2 // BestSelling + ShelvesChips
private fun synchronizeScrollsAndAlpha(
    index: Int,
    layoutInfo: LazyListLayoutInfo,
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    coroutineScope: CoroutineScope,
    lazyRowListState: LazyListState,
    chipsAlphaUpdate: (Float) -> Unit
) {
    val shelfIndex = index - SHELVES_OFFSET
    if (shelfIndex >= 0 && shelfIndex < state.shelves.items.size) {
        val shelfId = state.shelves.items[shelfIndex].id
        listener.onShelfClicked(shelfId)
        coroutineScope.launch {
            lazyRowListState.animateScrollToItem(shelfIndex)
        }
    }

    val isBestSellingVisible = layoutInfo.visibleItemsInfo
        .any { it.key == "BestSelling" }

    chipsAlphaUpdate(if (isBestSellingVisible) 0f else 1f)
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()
    val isScrolledToBottom = if (lastItem != null) {
        val isLastItem = lastItem.index == layoutInfo.totalItemsCount - 1
        val isCompletelyVisible = lastItem.offset + lastItem.size <= layoutInfo.viewportEndOffset
        isLastItem && isCompletelyVisible
    } else {
        false
    }

    chipsAlphaUpdate(if (isBestSellingVisible || isScrolledToBottom) 0f else 1f)
}