package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingHorizontalList
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeBestNearestDuknas
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BestNearestDukanSection(
    dukans : LazyPagingItems<MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    AnimatedContent(
        targetState = dukans.loadState.refresh,
    ) {
        when (it) {
            LoadState.Loading -> LoadingHorizontalList {
                LoadingBestNearDukanItem()
            }

            is LoadState.NotLoading -> {
                BestNearestDukanList(
                    dukans = dukans,
                    onDukanClick = onDukanClick,
                    modifier = modifier
                )
            }

            is LoadState.Error -> {}
        }
    }
}

@Composable
private fun BestNearestDukanList(
    dukans: LazyPagingItems<MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        ) {
        items(
            count = dukans.itemCount,
            key = { index -> dukans[index]?.id ?: index },
            contentType = { "BestNearDukanCard" },

            ) {
            val dukan = dukans[it] ?: return@items
            BestNearDukanCard(
                dukanName = dukan.name,
                imageUrl = dukan.imageUrl,
                onClick = { onDukanClick(dukan.id) }
            )
        }
    }
}

@Preview
@Composable
private fun BestNearestDukanSectionPreview() {
    MenaTheme {
        val fakePagingItems = flowOf(PagingData.from(fakeBestNearestDuknas()))
            .collectAsLazyPagingItems()
        BestNearestDukanList(
            dukans = fakePagingItems,
            onDukanClick = {}
        )
    }
}