package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.animation.AnimatedContent
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
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState

@Composable
fun NoImageDukanShelvesContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelves: LazyPagingItems<ShelfUiState>
) {
    val lazyRowListState = rememberLazyListState()
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var chipsAlpha by rememberSaveable { mutableStateOf(0f) }
    val layoutInfo by remember {
        derivedStateOf { lazyColumnListState.layoutInfo }
    }

    LaunchedEffect(layoutInfo) {
        synchronizeScrollsAndAlpha(
            lazyColumnListState.firstVisibleItemIndex,
            layoutInfo,
            shelves,
            listener,
            coroutineScope,
            lazyRowListState
        ) { alpha -> chipsAlpha = alpha }
    }

    AnimatedContent(
        targetState = shelves.loadState.refresh,
        label = "ShelvesContentAnimation"
    ) {
        when (it) {
            LoadState.Loading -> NoImageDukanShelvesContentSkeleton()
            is LoadState.NotLoading -> NoImageDukanShelvesContentLoaded(
                state = state,
                listener = listener,
                shelves = shelves,
                lazyColumnListState = lazyColumnListState,
                coroutineScope = coroutineScope,
                chipsAlpha = chipsAlpha
            )

            is LoadState.Error -> {}
        }
    }
}

@Composable
private fun NoImageDukanShelvesContentLoaded(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelves: LazyPagingItems<ShelfUiState>,
    lazyColumnListState: LazyListState,
    coroutineScope: CoroutineScope,
    chipsAlpha: Float
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = Theme.spacing._8),
        state = lazyColumnListState
    ) {
        if (state.bestSellingProducts.isNotEmpty()) {
            item(key = "BestSelling") {
                BestSellingNoImageDukan(
                    state = state,
                    listener = listener
                )
            }
        }
        stickyHeader(key = "ShelvesChips") {
            val SHELVES_OFFSET = 2 // BestSelling + ShelvesChips

            NoImageDukanShelvesChips(
                shelfs = shelves,
                onClick = { shelfId, index ->
                    listener.onShelfClicked(shelfId)
                    coroutineScope.launch {
                        lazyColumnListState.animateScrollToItem(index + SHELVES_OFFSET)
                    }
                },
                alpha = chipsAlpha,
                selectedShelfId = state.shelfIdSelected,
                dukanColor = state.dukanInfo.color
            )
        }

        items(count = shelves.itemCount, key = { shelves[it]?.id.orEmpty() }) {
            val shelf = shelves[it] ?: return@items
            val products = state.shelfProductsLimited[shelf.id] ?: emptyList()
            NoImageDukanShelfWithProducts(
                shelf = shelf,
                products = products,
                state = state,
                listener = listener
            )
        }
    }
}

private fun synchronizeScrollsAndAlpha(
    index: Int,
    layoutInfo: LazyListLayoutInfo,
    shelfs: LazyPagingItems<ShelfUiState>,
    listener: DukanDetailsInteractionListener,
    coroutineScope: CoroutineScope,
    lazyRowListState: LazyListState,
    chipsAlphaUpdate: (Float) -> Unit
) {
    val SHELVES_OFFSET = 2 // BestSelling + ShelvesChips

    val shelfIndex = index - SHELVES_OFFSET
    if (shelfIndex >= 0 && shelfIndex < shelfs.itemCount) {
        val shelfId = shelfs[shelfIndex]?.id.orEmpty()
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