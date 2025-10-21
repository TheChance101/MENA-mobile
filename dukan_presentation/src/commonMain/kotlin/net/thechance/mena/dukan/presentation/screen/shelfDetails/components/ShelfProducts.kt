package net.thechance.mena.dukan.presentation.screen.shelfDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.collectAsLazyPagingItems
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

    AnimatedContent(
        targetState = state.productsState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ProductContentAnimation"
    ) { target ->

        val product = state.productsShelf.collectAsLazyPagingItems().itemSnapshotList.items

        when (target) {
            ShelfDetailsUiState.ProductsState.LOADING -> {
                LoadingVerticalList {
                    LoadingProductCard()
                }
            }


            ShelfDetailsUiState.ProductsState.LOADED ->

                LazyColumn {
                    items(product) { product ->
                        ProductCard(
                            modifier = Modifier,
                            productName = product.name,
                            productImageUrl = product.imageUrl,
                            productDescription = product.description,
                            productCardBackground = productCardBackground,
                            productPrice = product.price,
                            productAction = {
                                CartProductAction(
                                    isVisible = isAddToCartVisible,
                                    style = state.dukanStyle,
                                    state = state,
                                    listener = listener,
                                    product = product
                                )
                            },
                        )
                    }
                }
            ShelfDetailsUiState.ProductsState.ERROR -> {}
        }
    }
}

@Composable
private fun CartProductAction(
    isVisible: Boolean,
    style: Style,
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    product: ShelfDetailsUiState.ProductUiState
) {
    if (isVisible.not()) return

    AnimatedContent(
        targetState = style,
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
    when (style) {
        Style.SMALL_IMAGE -> {
            ProductActionIconSmallImageDukan(
                inCartQuantity = state.productsShelf.collectAsLazyPagingItems().itemSnapshotList.items.first { it.id == product.id }.inCartQuantity,
                onAddClick = { listener.onAddToCartClick(product.id) },
                onPlusClick = { },
                onMinusClick = { },
                cartColor = Color(state.dukancolor)
            )
        }

        else -> {
            ProductActionNoImageDukan(
                inCartQuantity = state.productsShelf.collectAsLazyPagingItems().itemSnapshotList.items.first { it.id == product.id }.inCartQuantity,
                onAddClick = { listener.onAddToCartClick(product.id) },
                onPlusClick = { },
                onMinusClick = { },
                dukanColor = state.dukancolor,
            )
        }
    }
}