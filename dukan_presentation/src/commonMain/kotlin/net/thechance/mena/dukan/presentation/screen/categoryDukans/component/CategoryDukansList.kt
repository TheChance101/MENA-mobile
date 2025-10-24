package net.thechance.mena.dukan.presentation.screen.categoryDukans.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
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
        modifier = modifier
    ) {
        items(
            count = dukans.itemCount,
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