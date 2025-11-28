package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.product.SmallAndWideImageDukanProductAction
import net.thechance.mena.dukan.presentation.component.shared.ProductsHeader
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import org.jetbrains.compose.resources.painterResource

@Composable
fun SmallImageDukanShelvesContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelves: LazyPagingItems<ShelfUiState>,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        shelves.loadState.refresh
    ) {
        when (it) {
            LoadState.Loading -> SmallImageProductSkeleton()
            is LoadState.NotLoading -> ShelfContent(
                state = state,
                shelves = shelves,
                listener = listener,
                modifier = modifier
            )

            is LoadState.Error -> {}
        }
    }
}

@Composable
private fun ShelfContent(
    state: DukanDetailsUiState,
    shelves: LazyPagingItems<ShelfUiState>,
    listener: DukanDetailsInteractionListener,
    modifier: Modifier
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(
            bottom = Theme.spacing._16
        ),
    ) {
        if (state.bestSellingProducts.isNotEmpty()) {
            item(key = "BestSelling") {
                SmallImageDukanBestSellingSection(state, listener)
            }
        }
        items(
            count = shelves.itemCount,
            key = shelves.itemKey { it.id }
        ) { index ->
            shelves[index]?.let { shelf ->
                ProductsHeader(
                    viewAllColor = Color(state.dukanInfo.color),
                    shelfName = shelf.name,
                    onClick = { listener.onViewAllProductsShelfClicked(shelf.id, shelf.name) },
                    modifier = Modifier.padding(
                        start = Theme.spacing._16,
                        end = Theme.spacing._16,
                        bottom = Theme.spacing._8
                    )
                )
                ShelfProducts(
                    state = state,
                    products = state.shelfProductsLimited[shelf.id] ?: emptyList(),
                    listener = listener
                )
            }
        }
    }
}

@Composable
private fun ShelfProducts(
    state: DukanDetailsUiState,
    products: List<ProductUiState>,
    listener: DukanDetailsInteractionListener,
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val productPairs = remember(products) { products.chunked(2) }
        val lazyListState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = lazyListState,
            snapPosition = SnapPosition.Center
        )

        val screenWidth = maxWidth
        val cardMinWidth = 320.dp
        val spacing = Theme.spacing._8
        val cardsInRow = (screenWidth / (cardMinWidth + spacing)).toInt().coerceAtLeast(1)
        val cardWidth = if (cardsInRow == 1) screenWidth * 0.98f else
            (screenWidth - spacing * (cardsInRow - 1)) / cardsInRow

        LazyRow(
            modifier = Modifier.padding(bottom = Theme.spacing._8),
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = Theme.spacing._16),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            flingBehavior = flingBehavior
        ) {
            itemsIndexed(productPairs) { index, pair ->
                Column(
                    modifier = Modifier.width(cardWidth),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
                ) {
                    pair.forEach { product ->
                        key(product.id) {
                            ProductItem(
                                product = product,
                                cartColor = Color(state.dukanInfo.color),
                                quantity = state.productQuantity[product.id] ?: 0,
                                listener = listener
                            )
                        }
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
    cartColor: Color,
    quantity: Int
) {
    ProductCard(
        name = product.name,
        imageUrl = product.imageUrl,
        description = product.description,
        isOutOfStock = product.isOutOfStock,
        basePrice = product.basePrice,
        finalPrice = product.finalPrice,
        backgroundColor = Theme.colorScheme.background.surfaceLow,
        onProductClick = { listener.onProductClicked(product.id) },
        productAction = {
            if (product.isOutOfStock.not()) {
                SmallAndWideImageDukanProductAction(
                    showProductQuantity = quantity > 0,
                    inCartQuantity = quantity,
                    dukanColor = cartColor,
                    cartIcon = painterResource(Res.drawable.ic_add_shopping_basket),
                    onAddToCartClick = {
                        listener.onAddToCartClicked(
                            productId = product.id,
                            productQuantity = quantity + 1
                        )
                    },
                    onPlusClick = {
                        listener.onPlusClicked(
                            productId = product.id,
                            productQuantity = quantity + 1
                        )
                    },
                    onMinusClick = {
                        listener.onMinusClicked(
                            productId = product.id,
                            productQuantity = quantity - 1
                        )
                    }
                )
            }
        }
    )
}