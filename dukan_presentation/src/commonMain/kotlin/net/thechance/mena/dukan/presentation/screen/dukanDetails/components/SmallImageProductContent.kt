package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.FlingBehavior
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ProductsHeader
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState

@Composable
fun SmallImageProductContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelvesPager: Pager<Int, DukanDetailsUiState.ShelfUiState>,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    lazyListState.LoadMoreOnScroll(shelvesPager)

    AnimatedContent(
        targetState = state.shelvesState
    ) {
        when (it) {
            DukanDetailsUiState.ShelvesState.LOADING -> {
                SmallImageProductLoadingContent()
            }
            DukanDetailsUiState.ShelvesState.LOADED -> {
                SmallImageProductLoadedContent(modifier, lazyListState, listener, state)
            }
            DukanDetailsUiState.ShelvesState.EMPTY -> {}
        }
    }
}

@Composable
fun SmallImageProductLoadedContent(
    modifier: Modifier,
    lazyListState: LazyListState,
    listener: DukanDetailsInteractionListener,
    state: DukanDetailsUiState
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Theme.spacing._16),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        state.shelves.items.forEach { shelf ->
            item(key = shelf.id) {
                ProductsHeader(
                    viewAllColor = Color(state.dukanInfo.color),
                    shelfName = shelf.name,
                    onClick = {
                        listener.onViewAllShelfProductsClicked(
                            shelf.id,
                            shelf.name
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = Theme.spacing._16)
                )
            }
            item {
                ProductsShelf(
                    shelf = shelf,
                    cartColor = Color(state.dukanInfo.color),
                    listener = listener
                )
            }
        }
    }
}

@Composable
private fun ProductsShelf(
    shelf: DukanDetailsUiState.ShelfUiState,
    listener: DukanDetailsInteractionListener,
    cartColor: Color? = null
) {
    val productPairs = remember(shelf.products) { shelf.products.chunked(2) }
    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = lazyListState,
        snapPosition = SnapPosition.Center
    )

    ProductsLazyRow(
        productPairs = productPairs,
        lazyListState = lazyListState,
        flingBehavior = flingBehavior,
        listener = listener,
        cartColor = cartColor
    )
}

@Composable
private fun ProductsLazyRow(
    productPairs: List<List<DukanDetailsUiState.ProductUiState>>,
    lazyListState: LazyListState,
    flingBehavior: FlingBehavior,
    listener: DukanDetailsInteractionListener,
    cartColor: Color?
) {
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
    product: DukanDetailsUiState.ProductUiState,
    listener: DukanDetailsInteractionListener,
    cartColor: Color?
) {
    val isCartButtonVisible = false // TODO: Remove when implement Cart
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