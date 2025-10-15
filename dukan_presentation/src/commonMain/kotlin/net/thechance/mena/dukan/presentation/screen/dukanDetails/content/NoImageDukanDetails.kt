package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle.AppBarNoImageDukan
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle.BestSellingNoImageDukan
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle.ShelvesChipsNoImageDukan
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle.shelvesWithProductsNoImageDukan
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeShelvesDukanDetailsPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeShelves
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>,
) {
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            AppBarNoImageDukan(
                state = state.dukanInfo,
                listener = listener
            )
        }
    ) {
        NoImageDukanContent(
            state = state,
            listener = listener,
            pagerShelves = pagerShelves
        )
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
        if (shelfId != state.shelfIdSelected) {
            listener.onShelfClicked(shelfId)
            coroutineScope.launch {
                lazyRowListState.animateScrollToItem(shelfIndex)
            }
        }
    }

    val isBestSellingVisible = layoutInfo.visibleItemsInfo
        .any { it.key == "BestSelling" }

    chipsAlphaUpdate(if (isBestSellingVisible) 0f else 1f)
}

@OptIn(FlowPreview::class)
@Composable
private fun NoImageDukanContent(
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
    LaunchedEffect(key1 = lazyColumnListState) {
        snapshotFlow { lazyColumnListState.firstVisibleItemIndex to lazyColumnListState.layoutInfo }
            .debounce { 10L }
            .collect { (index, layoutInfo) ->
                synchronizeScrollsAndAlpha(
                    index,
                    layoutInfo,
                    state,
                    listener,
                    coroutineScope,
                    lazyRowListState
                ) { alpha -> chipsAlpha = alpha }
            }
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
            ShelvesChipsNoImageDukan(
                state = state,
                lazyRowState = lazyRowListState,
                onClickListener = { shelfId, index ->
                    listener.onShelfClicked(shelfId)
                    coroutineScope.launch {
                        lazyColumnListState.animateScrollToItem(index + SHELVES_OFFSET)
                    }
                },
                alpha = chipsAlpha
            )
        }
        shelvesWithProductsNoImageDukan(
            state = state,
            listener = listener
        )
    }
}


@Preview
@Composable
private fun NoImageDukanDetailsPreview() {
    var previewState by remember {
        mutableStateOf(
            DukanDetailsUiState(
                DukanInfo(
                    name = "Calvin Klein store international",
                    color = 0xFFFB5B5D
                ),
                shelvesState = ShelvesState.LOADED,
                shelfIdSelected = "1",
                shelves = PagingData(fakeShelves())
            )
        )
    }

    val previewListener =
        object : DukanDetailsInteractionListener by PreviewDukanDetailsInteractionListener {
            override fun onShelfClicked(id: String) {
                previewState = previewState.copy(
                    shelfIdSelected = id,
                )
            }
        }
    MenaTheme {
        NoImageDukanDetails(
            state = previewState,
            listener = previewListener,
            pagerShelves = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeShelvesDukanDetailsPagingSource() }
            ),
        )
    }
}