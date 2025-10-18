package net.thechance.mena.dukan.presentation.screen.dukanDetails.content.wideImageDukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.products
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ShelfChip
import net.thechance.mena.dukan.presentation.util.modifiers.fillWidthOfParent
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeShelvesDukanDetailsPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanShelvesSection(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelvesPager: Pager<Int, DukanDetailsUiState.ShelfUiState>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = stringResource(Res.string.products),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._16)
        )
        AnimatedContent(
            targetState = state.shelvesState,
            transitionSpec = {
                fadeIn(
                    tween(300)
                ) togetherWith fadeOut(tween(300))
            },
            label = "Shelves Animation",
        ) { targetState ->
            when (targetState) {
                DukanDetailsUiState.ShelvesState.LOADING -> LoadingShelves()
                DukanDetailsUiState.ShelvesState.LOADED -> LoadedShelves(
                    shelves = state.shelves.items,
                    selectedShelfId = state.shelfIdSelected,
                    onShelfClick = listener::onShelfClicked,
                    chipColor = Color(state.dukanInfo.color),
                    shelvesPager = shelvesPager
                )

                DukanDetailsUiState.ShelvesState.EMPTY -> {}
            }
        }
    }
}

@Composable
private fun LoadingShelves() {
    val shelvesCount = 8
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        modifier = Modifier.fillWidthOfParent(Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(count = shelvesCount) {
            Chip(
                text = "             ",
                isSelected = false,
                isEnabled = false,
                onClick = {})
        }
    }
}

@Composable
private fun LoadedShelves(
    shelves: List<DukanDetailsUiState.ShelfUiState>,
    selectedShelfId: String?,
    onShelfClick: (shelfId: String) -> Unit,
    chipColor: Color,
    shelvesPager: Pager<Int, DukanDetailsUiState.ShelfUiState>
) {
    val listState = rememberLazyListState()
    listState.LoadMoreOnScroll(shelvesPager)
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        modifier = Modifier.fillWidthOfParent(Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(items = shelves, key = { it.id }) { shelf ->
            ShelfChip(
                text = shelf.name,
                isSelected = (shelf.id == selectedShelfId),
                onClick = { onShelfClick(shelf.id) },
                selectedBackgroundColor = chipColor,
            )
        }
    }
}

@Preview(showBackground = true, name = "Shelves Section - Loading")
@Composable
private fun DukanShelvesSectionLoadingPreview() {
    MenaTheme {
        val dummyPager = Pager<Int, DukanDetailsUiState.ShelfUiState>(
            config = PagingConfig(),
            pagingSourceFactory = { FakeShelvesDukanDetailsPagingSource() }
        )
        DukanShelvesSection(
            state = DukanDetailsUiState(shelvesState = DukanDetailsUiState.ShelvesState.LOADING),
            listener = PreviewDukanDetailsInteractionListener,
            shelvesPager = dummyPager
        )
    }
}

@Preview(showBackground = true, name = "Shelves Section - Loaded")
@Composable
private fun DukanShelvesSectionLoadedPreview() {
    MenaTheme {
        val dummyPager = Pager<Int, DukanDetailsUiState.ShelfUiState>(
            config = PagingConfig(),
            pagingSourceFactory = { FakeShelvesDukanDetailsPagingSource() }
        )
        DukanShelvesSection(
            state = DukanDetailsUiState(
                shelvesState = DukanDetailsUiState.ShelvesState.LOADED,
                shelves = PagingData(
                    items = listOf(
                        DukanDetailsUiState.ShelfUiState(id = "1", name = "Dairy & Eggs"),
                        DukanDetailsUiState.ShelfUiState(id = "2", name = "Fresh Produce"),
                        DukanDetailsUiState.ShelfUiState(id = "3", name = "Bakery"),
                    )
                ),
                shelfIdSelected = "2",
                dukanInfo = DukanDetailsUiState.DukanInfo(color = 0xFF4CAF50)
            ),
            listener = PreviewDukanDetailsInteractionListener,
            shelvesPager = dummyPager
        )
    }
}