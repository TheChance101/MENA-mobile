package net.thechance.mena.dukan.presentation.screen.categoryDukans.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
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
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16)
    ) {
        items(
            count = dukans.itemCount,
            key = dukans.itemKey { it.id }
        ) { index ->
            val dukan = dukans[index] ?: return@items
            DukanCard(
                dukan = dukan,
                isFavorite = dukan.isFavorite,
                onClick = { listener.onDukanClick(dukan) },
                onFavoriteClick = { listener.onFavoriteClick(dukan) },
                isLoading = isLoading
            )
        }
    }
}