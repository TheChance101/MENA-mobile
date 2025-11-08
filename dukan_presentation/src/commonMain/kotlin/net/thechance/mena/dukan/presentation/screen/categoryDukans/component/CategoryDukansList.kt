package net.thechance.mena.dukan.presentation.screen.categoryDukans.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDukanPlaceholder
import net.thechance.mena.dukan.presentation.component.shared.DukanCard
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState

@Composable
fun CategoryDukansList(
    dukans: LazyPagingItems<CategoryDukansUiState.DukanUiState>,
    listener: CategoryDukansInteractionListener,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(328.dp),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16)
    ) {
        items(
            count = dukans.itemCount,
            key = dukans.itemKey { it.id }
        ) { index ->
            val dukan = dukans[index] ?: return@items
            DukanCard(
                title = dukan.name,
                imageUrl = dukan.imageUrl,
                isFavorite = dukan.isFavorite,
                onClick = { listener.onDukanClicked(dukan) },
                onFavoriteClick = { listener.onFavoriteDukanClicked(dukan.id) },
                isLoading = isLoading
            )
        }
        if (isLoading)
            items(8) {
                LoadingDukanPlaceholder()
            }
    }
}