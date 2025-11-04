package net.thechance.mena.dukan.presentation.screen.shelfDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.loading.LoadingVerticalList
import net.thechance.mena.dukan.presentation.component.product.ProductActionIconSmallImageDukan
import net.thechance.mena.dukan.presentation.component.product.ProductActionNoImageDukan
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState.Style

@Composable
fun ShelfProducts(
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
) {
    val dukanStyle = state.dukanStyle
    val productCardBackground = if (dukanStyle == Style.NO_IMAGE) null
    else Theme.colorScheme.background.surfaceLow

    val isAddToCartVisible = false // TODO: Remove when implement the Cart

    val products = state.productsShelf.collectAsLazyPagingItems()
    AnimatedContent(
        targetState = products.loadState.refresh,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ProductContentAnimation"
    ) { target ->
        when (target) {
            LoadState.Loading -> LoadingVerticalList { LoadingProductCard() }
            is LoadState.NotLoading -> ProductCardLoaded(
                productCardBackground = productCardBackground,
                isAddToCartVisible = isAddToCartVisible,
                products = products,
                listener = listener,
                state = state
            )
            is LoadState.Error -> {}
        }
    }
}

@Composable
private fun ProductCardLoaded(
    productCardBackground : Color?,
    isAddToCartVisible: Boolean,
    products: LazyPagingItems<ShelfDetailsUiState.ProductUiState>,
    listener: ShelfDetailsInteractionListener,
    state : ShelfDetailsUiState
){
    LazyColumn(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            products.itemCount,
            key = products.itemKey { it.id }
        ) { index ->
            products[index]?.let { product ->
                ProductCard(
                    productName = product.name,
                    productImageUrl = product.imageUrl,
                    productDescription = product.description,
                    productCardBackground = productCardBackground,
                    productPrice = product.price,
                    productAction = {
                        CartProductAction(
                            isVisible = isAddToCartVisible,
                            state = state,
                            listener = listener,
                            product = product
                        )
                    },
                    onClick = {listener.onProductClicked(product.id)},
                                    )
            }
        }
    }
}
@Composable
private fun CartProductAction(
    isVisible: Boolean,
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    product: ShelfDetailsUiState.ProductUiState
) {
    if (isVisible.not()) return

    AnimatedContent(
        targetState = state.dukanStyle,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "CartProductIconAnimation"
    ) { currentStyle ->
        GetProductIconAction(
            style = currentStyle,
            state = state,
            listener = listener,
            product = product
        )
    }
}

@Composable
private fun GetProductIconAction(
    style: Style,
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    product: ShelfDetailsUiState.ProductUiState
) {
    val lazyItems = state.productsShelf.collectAsLazyPagingItems()
    val inCartQuantity = lazyItems.itemSnapshotList.items
        .firstOrNull { it.id == product.id }
        ?.inCartQuantity ?: 0

    when (style) {
        Style.SMALL_IMAGE -> {
            ProductActionIconSmallImageDukan(
                inCartQuantity = inCartQuantity,
                onAddClick = { listener.onAddToCartClicked(product.id) },
                onPlusClick = { },
                onMinusClick = { },
                cartColor = Color(state.dukancolor)
            )
        }

        else -> {
            ProductActionNoImageDukan(
                inCartQuantity = inCartQuantity,
                onAddClick = { listener.onAddToCartClicked(product.id) },
                onPlusClick = { },
                onMinusClick = { },
                dukanColor = state.dukancolor,
            )
        }
    }
}