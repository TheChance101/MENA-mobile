package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.BestSellingSection
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.WideImageDukanAppBar
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.WideImageDukanHeader
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.WideImageDukanShelves
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.wideImageProductCardSkeletonGrid
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails.wideImageProductsGrid
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WideImageDukanDetailsContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
) {
    val shelves = state.shelves.collectAsLazyPagingItems()
    val productShelf = state.productsShelf.collectAsLazyPagingItems()

    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            WideImageDukanAppBar(
                isBadgeVisible = state.hasProductInCart,
                onBackClicked = listener::onBackClicked,
                onCartClicked = listener::onViewCartClicked
            )
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onDismissSnackBar,
                    onClick = listener::onDismissSnackBar
                )
            }
        }
    ) {
        if (state.dukanDetailsState == DukanDetailsUiState.DukanDetailsState.ERROR) {
            NoInternetContent(
                onRetry = listener::onRetryClicked,
                modifier = Modifier.fillMaxSize()
            )
            return@Scaffold
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(
                start = Theme.spacing._16,
                end = Theme.spacing._16,
                top = Theme.spacing._4,
                bottom = Theme.spacing._16
            ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._12)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                WideImageDukanHeader(
                    state = state.dukanInfo,
                    onFavoriteClicked = listener::onFavoriteDukanClicked,
                )
            }
            if (productShelf.loadState.refresh is LoadState.NotLoading) {

                if (state.bestSellingProducts.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        BestSellingSection(
                            state = state,
                            listener = listener,
                            shelves = shelves
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                WideImageDukanShelves(
                    state = state,
                    listener = listener,
                    shelves = shelves
                )
            }
            when (productShelf.loadState.refresh) {
                is LoadState.Loading -> wideImageProductCardSkeletonGrid(productCount = 6)
                is LoadState.NotLoading -> {
                    wideImageProductsGrid(
                        state = state,
                        listener = listener,
                        productsShelf = productShelf
                    )
                }

                is LoadState.Error -> {}
            }
        }
    }
}

@Preview(showBackground = true, name = "Full Screen Preview")
@Composable
private fun WideImageDukanDetailsPreview() {
    MenaTheme {
        WideImageDukanDetailsContent(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener
        )
    }
}
