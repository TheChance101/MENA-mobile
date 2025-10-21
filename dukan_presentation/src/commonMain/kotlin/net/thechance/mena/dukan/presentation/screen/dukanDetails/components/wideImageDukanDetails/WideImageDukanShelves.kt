package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

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
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakePagerShelvesDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WideImageDukanShelves(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelvesPager: Pager<Int, ShelfUiState>
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
                ShelvesState.LOADING -> LoadingShelves()
                ShelvesState.LOADED -> LoadedShelves(
                    shelves = state.shelves.items,
                    selectedShelfId = state.shelfIdSelected,
                    onShelfClick = listener::onShelfClicked,
                    chipColor = Color(state.dukanInfo.color),
                    pagerShelves = shelvesPager
                )

                ShelvesState.EMPTY -> {}
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
                onClick = {}
            )
        }
    }
}

@Composable
private fun LoadedShelves(
    shelves: List<ShelfUiState>,
    selectedShelfId: String?,
    chipColor: Color,
    pagerShelves: Pager<Int, ShelfUiState>,
    onShelfClick: (shelfId: String) -> Unit
) {
    val listState = rememberLazyListState()
    listState.LoadMoreOnScroll(pagerShelves)
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
        WideImageDukanShelves(
            state = DukanDetailsUiState(shelvesState = ShelvesState.LOADING),
            listener = PreviewDukanDetailsInteractionListener,
            shelvesPager = fakePagerShelvesDukanDetails
        )
    }
}

@Preview(showBackground = true, name = "Shelves Section - Loaded")
@Composable
private fun DukanShelvesSectionLoadedPreview() {
    MenaTheme {
        WideImageDukanShelves(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
            shelvesPager = fakePagerShelvesDukanDetails
        )
    }
}