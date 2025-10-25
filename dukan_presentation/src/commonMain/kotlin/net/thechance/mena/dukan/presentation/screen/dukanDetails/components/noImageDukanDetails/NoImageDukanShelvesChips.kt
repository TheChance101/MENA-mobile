package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.chip.ShelfChip
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState

@Composable
fun NoImageDukanShelvesChips(
    shelfs: LazyPagingItems<DukanDetailsUiState.ShelfUiState>,
    selectedShelfId: String?,
    dukanColor: Long,
    onClick: (String, Int) -> Unit,
    alpha: Float = 1f
) {

    if (alpha == 0f) {
        return
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Theme.spacing._4)
            .alpha(alpha)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface)
                .padding(vertical = Theme.spacing._8),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        ) {
            items(
                count = shelfs.itemCount,
                key = shelfs.itemKey { it.id }
            ) { index ->
                shelfs[index]?.let { shelf ->
                    ShelfChip(
                        text = shelf.name,
                        isSelected = (shelf.id == selectedShelfId),
                        onClick = { onClick(shelf.id, index) },
                        selectedBackgroundColor = Color(dukanColor)
                    )
                }
            }
        }
    }
}