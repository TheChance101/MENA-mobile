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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
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
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val shelves = state.shelves.collectAsLazyPagingItems()

    AnimatedContent(
        shelves.loadState.refresh
    ) {
        when (it) {
            LoadState.Loading -> SmallImageProductSkeleton()
            is LoadState.NotLoading -> ShelfContent(
                shelves = shelves,
                dukanColor = Color(state.dukanInfo.color),
                lazyListState = lazyListState,
                listener = listener,
                modifier = modifier,
            )

            is LoadState.Error -> {}
        }
    }
}

@Composable
private fun ShelfContent(
    shelves: LazyPagingItems<ShelfUiState>,
    dukanColor: Color,
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
                    viewAllColor = dukanColor,
                    shelfName = shelf.name,
                    onClick = { listener.onViewAllProductsShelfClicked(shelf.id, shelf.name) },
                    modifier = Modifier.padding(
                        start = Theme.spacing._16,
                        end = Theme.spacing._16,
                        bottom = Theme.spacing._8
                    )
                )
                ShelfProducts(
                    shelf = shelf,
                    listener = listener,
                    cartColor = dukanColor
                )
            }
        }
    }
}

@Composable
private fun ShelfProducts(
    shelf: ShelfUiState,
    listener: DukanDetailsInteractionListener,
    cartColor: Color
) {

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val productPairs = remember(shelf.products) { shelf.products.chunked(2) }
        val lazyListState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = lazyListState,
            snapPosition = SnapPosition.Center
        )

        val screenWidth = maxWidth
        val horizontalSpacing =
            Theme.spacing._8 * 2 + Theme.spacing._16 * 2
        val cardWidth = when {
            screenWidth < 500.dp -> screenWidth - horizontalSpacing - 40.dp
            screenWidth < 800.dp -> screenWidth * 0.45f
            else -> screenWidth * 0.3f
        }

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
                                cartColor = cartColor,
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
    cartColor: Color
) {
    var toggleCartToQuantity by rememberSaveable { mutableStateOf(product.inCartQuantity > 0) }
    var productQuantity by rememberSaveable { mutableIntStateOf(product.inCartQuantity) }

    LaunchedEffect(product) {
        toggleCartToQuantity = product.inCartQuantity > 0
        productQuantity = product.inCartQuantity
    }

    ProductCard(
        productName = product.name,
        productImageUrl = product.imageUrl,
        productDescription = product.description,
        productPrice = product.price,
        productCardBackground = Theme.colorScheme.background.surfaceLow,
        onProductClick = { listener.onProductClicked(product.id) },
        productAction = {
            SmallAndWideImageDukanProductAction(
                showProductQuantity = toggleCartToQuantity,
                inCartQuantity = productQuantity,
                dukanColor = cartColor,
                cartIcon = painterResource(Res.drawable.ic_add_shopping_basket),
                onAddToCartClick = {
                    toggleCartToQuantity = true
                    productQuantity += 1
                    listener.onAddToCartClicked(
                        productId = product.id,
                        productQuantity = productQuantity
                    )
                },
                onPlusClick = {
                    productQuantity += 1
                    listener.onPlusClicked(
                        productId = product.id,
                        productQuantity = productQuantity
                    )
                },
                onMinusClick = {
                    if (productQuantity == 1) toggleCartToQuantity = false
                    productQuantity -= 1
                    listener.onMinusClicked(
                        productId = product.id,
                        productQuantity = productQuantity
                    )
                }
            )
        }
    )
}