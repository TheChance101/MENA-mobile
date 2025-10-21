package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.DukanHeader
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.wideImageProductsGrid
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.WideImageDukanAppBar
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.WideImageDukanShelves
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakePagerProductsDukanDetails
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakePagerShelvesDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WideImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelf: Pager<Int, DukanDetailsUiState.ShelfUiState>,
    pagerProduct: Pager<Int, DukanDetailsUiState.ProductUiState>
) {
    OnSystemBackPressed(listener::onBackClicked)

    val gridState = rememberLazyGridState()
    gridState.LoadMoreOnScroll(pagerProduct)
    Scaffold(
        topBar = {
            WideImageDukanAppBar(
                onBackClicked = listener::onBackClicked,
                onCartClicked = {}
            )
        }
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(
                start = Theme.spacing._16,
                end = Theme.spacing._16,
                top = Theme.spacing._4,
                bottom = Theme.spacing._16
            ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._16),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DukanHeader(state = state.dukanInfo)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                WideImageDukanShelves(state = state, listener = listener, shelvesPager = pagerShelf)
            }
            wideImageProductsGrid(state = state)
        }
    }
}

@Preview(showBackground = true, name = "Full Screen Preview")
@Composable
private fun WideImageDukanDetailsPreview() {
    MenaTheme {
        WideImageDukanDetails(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
            pagerShelf = fakePagerShelvesDukanDetails,
            pagerProduct = fakePagerProductsDukanDetails,
        )
    }
}