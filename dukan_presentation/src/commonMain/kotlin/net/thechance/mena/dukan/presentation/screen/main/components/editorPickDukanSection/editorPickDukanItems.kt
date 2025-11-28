package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDukanPlaceholder
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

fun LazyGridScope.editorPickDukanItems(
    dukans: LazyPagingItems<MainScreenUiState.EditorPickDukanUiState>,
    onDukanClick: (String) -> Unit,
    onClickFavorite: (dukanId: String) -> Unit
) {

    when (dukans.loadState.refresh) {
        LoadState.Loading -> item {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
            ) {
                repeat(8) { LoadingDukanPlaceholder() }
            }
        }

        is LoadState.NotLoading -> {
            items(
                count = dukans.itemCount,
                contentType = { "EditorPickDukanItem" }
            ) { index ->
                val dukan = dukans[index] ?: return@items
                EditorPickDukanItem(
                    dukanName = dukan.name,
                    dukanImage = dukan.imageUrl,
                    onClickDukan = { onDukanClick(dukan.id) },
                    isFavorite = dukan.isFavorite,
                    onClickFavorite = { onClickFavorite(dukan.id) },
                    modifier = Modifier.padding(
                        vertical = Theme.spacing._8
                    )
                )
            }
        }

        is LoadState.Error -> {}
    }
}


@Preview()
@Composable
private fun EditorPickDukanItemsListPreview() {
    MenaTheme {
        val fakePagingItems = flowOf(PagingData.from(fakeDukans()))
            .collectAsLazyPagingItems()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(328.dp),
        ) {
            editorPickDukanItems(
                dukans = fakePagingItems,
                onClickFavorite = { },
                onDukanClick = {}
            )
        }
    }
}
