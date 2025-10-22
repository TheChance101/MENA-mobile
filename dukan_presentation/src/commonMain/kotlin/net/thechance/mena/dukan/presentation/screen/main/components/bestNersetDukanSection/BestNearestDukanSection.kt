package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.shared.LazyRowItems
import net.thechance.mena.dukan.presentation.component.loading.LoadingHorizontalList
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewBestNearestDukanPagingSource
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
                LoadingHorizontalList {
                    LoadingBestNearDukanItem()
                }
            }

            MainScreenUiState.BestNearestDukanStatus.LOADED -> {
                BestNearestDukanList(
                    dukans = state.bestNearestDukans.items,
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
    dukans: List<MainScreenUiState.BestNearestDukanUiState>,
    pager: Pager<Int, MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRowItems(
        items = dukans,
        pager = pager,
        key = { it.id },
        contentType = { "BestNearDukanCard" },
        modifier = modifier.fillMaxWidth(),
    ) { dukan ->
        BestNearDukanCard(
            dukanName = dukan.name,
            imageUrl = dukan.imageUrl,
            onClick = { onDukanClick(dukan.id) }
        )
    }
}

@Preview
@Composable
private fun BestNearestDukanSectionPreview() {
    MenaTheme {
        BestNearestDukanList(
            dukans = fakeBestNearestDuknas(),
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { PreviewBestNearestDukanPagingSource }
            ),
            onDukanClick = {}
        )
    }
}