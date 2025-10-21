package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.loading.LoadingHorizontalList
import net.thechance.mena.dukan.presentation.component.shared.LazyRowItems
import net.thechance.mena.dukan.presentation.util.pagination.PagerOld
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfigOld
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewBestNearestDukanPagingSourceOld
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeBestNearestDuknas
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BestNearestDukanSection(
    state: MainScreenUiState,
    pagerOld: PagerOld<Int, MainScreenUiState.BestNearestDukanUiState>,
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
                    pagerOld = pagerOld,
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
    pagerOld: PagerOld<Int, MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRowItems(
        items = dukans,
        pagerOld = pagerOld,
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
            pagerOld = PagerOld(
                config = PagingConfigOld(),
                pagingSourceOldFactory = { PreviewBestNearestDukanPagingSourceOld }
            ),
            onDukanClick = {}
        )
    }
}