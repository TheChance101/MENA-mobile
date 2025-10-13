package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.BestNearestDukanPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeBestNearestDuknas
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BestNearestDukanSection(
    state: MainScreenUiState,
    pager: Pager<Int, MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state.bestNearestDukanState,
    ) {
        when (it) {
            MainScreenUiState.BestNearestDukanStatus.LOADING -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = Theme.spacing._8),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(
                        horizontal = Theme.spacing._16,
                        vertical = Theme.spacing._8
                    )
                ) {
                    items(8) {
                        LoadingBestNearDukanItem()
                    }
                }
            }

            MainScreenUiState.BestNearestDukanStatus.LOADED -> {
                BestNearestDukanList(
                    dukans = state.bestNearestDukans,
                    pager = pager,
                    onDukanClick = onDukanClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun BestNearestDukanList(
    dukans: PagingData<MainScreenUiState.BestNearestDukanUiState>,
    pager: Pager<Int, MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val lazyListState = rememberLazyListState()
    lazyListState.LoadMoreOnScroll(pager)

    LazyRow(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            items = dukans.items,
            key = { it.id },
            contentType = { "BestNearDukanCard" }
        ) { dukan ->
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
        BestNearestDukanList(
            dukans = PagingData(items = fakeBestNearestDuknas()),
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { BestNearestDukanPagingSource }
            ),
            onDukanClick = {}
        )
    }
}