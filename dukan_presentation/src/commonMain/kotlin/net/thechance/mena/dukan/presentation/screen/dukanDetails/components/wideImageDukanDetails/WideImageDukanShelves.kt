package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.products
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.chip.ShelfChip
import net.thechance.mena.dukan.presentation.util.modifiers.fillWidthOfParent
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun WideImageDukanShelves(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelves: LazyPagingItems<ShelfUiState>
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
            targetState = shelves.loadState.refresh,
        ) {
            when (it) {
                LoadState.Loading -> LoadingShelves()
                is LoadState.NotLoading -> LoadedShelves(
                    shelves = shelves,
                    selectedShelfId = state.shelfIdSelected,
                    onShelfClick = listener::onShelfClicked,
                    chipColor = Color(state.dukanInfo.color),
                )

                is LoadState.Error -> {}
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
    shelves: LazyPagingItems<ShelfUiState>,
    selectedShelfId: String?,
    chipColor: Color,
    onShelfClick: (shelfId: String) -> Unit
) {
    if (selectedShelfId.isNullOrEmpty() && shelves.itemCount != 0) {
        onShelfClick(shelves.peek(0)?.id ?: "")
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        modifier = Modifier.fillWidthOfParent(Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            shelves.itemCount,
            key = shelves.itemKey { it.id }
        ) { index ->
            val shelf = shelves[index] ?: return@items
            ShelfChip(
                text = shelf.name,
                isSelected = (shelf.id == selectedShelfId),
                onClick = { onShelfClick(shelf.id) },
                selectedBackgroundColor = chipColor,
            )
        }
    }
}