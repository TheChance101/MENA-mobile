package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.ProductActionIconSmallImageDukan
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.ProductsHeader
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState

@Composable
fun SmallImageDukanShelves(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val shelves = state.shelves.collectAsLazyPagingItems()

    AnimatedContent(
        shelves.loadState.refresh
    ) {
        when (it) {
            LoadState.Loading -> SmallImageProductSkeleton()
            is LoadState.NotLoading -> ShelvesContent(
                shelves = shelves,
                dukanInfo = state.dukanInfo,
                listener = listener,
                lazyListState = lazyListState,
                modifier = modifier
            )

            is LoadState.Error -> {}
        }
    }
}

@Composable
private fun ShelvesContent(
    shelves: LazyPagingItems<ShelfUiState>,
    dukanInfo: DukanDetailsUiState.DukanInfo,
    listener: DukanDetailsInteractionListener,
    lazyListState: LazyListState,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(vertical = Theme.spacing._16),
    ) {
        items(
            count = shelves.itemCount,
            key = shelves.itemKey { it.id }
        ) { index ->
            shelves[index]?.let { shelf ->
                ProductsHeader(
                    viewAllColor = Color(dukanInfo.color),
                    shelfName = shelf.name,
                    onClick = {
                        listener.onViewAllProductsShelfClicked(
                            shelf.id,
                            shelf.name
                        )
                    },
                    modifier = Modifier.padding(
                        start = Theme.spacing._16,
                        end = Theme.spacing._16,
                        bottom = Theme.spacing._8
                    )
                )
                ShelfProducts(
                    shelf = shelf,
                    listener = listener,
                    cartColor = Color(dukanInfo.color)
                )
            }
        }
    }
}

@Composable
private fun ShelfProducts(
    shelf: ShelfUiState,
    listener: DukanDetailsInteractionListener,
    cartColor: Color? = null
) {
    val productPairs = remember(shelf.products) { shelf.products.chunked(2) }
    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = lazyListState,
        snapPosition = SnapPosition.Center
    )

    LazyRow(
        modifier = Modifier.padding(bottom = Theme.spacing._8),
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        flingBehavior = flingBehavior
    ) {
        itemsIndexed(productPairs) { _, pair ->
            Column(
                modifier = Modifier.fillParentMaxWidth(0.95f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
            ) {
                pair.forEach { product ->
                    key(product.id) {
                        ProductItem(product = product, listener = listener, cartColor = cartColor)
                    }
                }
            }
        }
    }
}


@Composable
private fun ProductItem(
    product: ProductUiState,
    listener: DukanDetailsInteractionListener,
    cartColor: Color?
) {
    val isCartButtonVisible = false  // TODO: Remove when implement Cart
    ProductCard(
        productName = product.name,
        productImageUrl = product.imageUrl,
        productDescription = product.description,
        productPrice = product.price,
        productCardBackground = Theme.colorScheme.background.surfaceLow,
        productAction = {
            if (isCartButtonVisible) {
                ProductActionIconSmallImageDukan(
                    inCartQuantity = product.inCartQuantity,
                    cartColor = cartColor,
                    onAddClick = { listener.onAddToCartClick(product.id) },
                    onPlusClick = {},
                    onMinusClick = {}
                )
            }
        }
    )
}