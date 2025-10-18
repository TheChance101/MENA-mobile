package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState.Style

@Composable
fun ShelfProducts(
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    pager: Pager<Int, ShelfDetailsUiState.ProductUiState>,
) {
    val dukanStyle = state.dukanStyle
    val productCardBackground = if (dukanStyle == Style.NO_IMAGE) {
        null
    } else {
        Theme.colorScheme.background.surfaceLow
    }
    val isAddToCartVisible = false // TODO: Remove when implement the Cart

    AnimatedContent(
        targetState = state.productsState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ProductContentAnimation"
    ) { target ->
        when (target) {
            ShelfDetailsUiState.ProductsState.LOADING -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = Theme.spacing._8),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(
                        horizontal = Theme.spacing._16,
                        vertical = Theme.spacing._8
                    ),
                ) {
                    items(8) {
                        LoadingProductCard()
                    }
                }
            }

            ShelfDetailsUiState.ProductsState.LOADED -> ProductsList(
                products = state.productsShelf,
                pager = pager,
                productCardBackground = productCardBackground,
                cartProductIcon = { productId ->
                    if (isAddToCartVisible)
                        AnimatedContent(
                            targetState = state.dukanStyle,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "CartProductIconAnimation"
                        ) { style ->
                            when (style) {
                                Style.SMALL_IMAGE -> {
                                    ProductActionIconSmallImageDukan(
                                        inCartQuantity = state.productsShelf.items.first { it.id == productId }.inCartQuantity,
                                        onAddClick = { listener.onAddToCartClick(productId) },
                                        onPlusClick = { },
                                        onMinusClick = { },
                                        cartColor = Color(state.dukancolor)
                                    )
                                }

                                else -> {
                                    ProductActionNoImageDukan(
                                        inCartQuantity = state.productsShelf.items.first { it.id == productId }.inCartQuantity,
                                        onAddClick = { listener.onAddToCartClick(productId) },
                                        onPlusClick = { },
                                        onMinusClick = { },
                                        dukanColor = state.dukancolor,
                                    )
                                }
                            }
                        }
                }
            )

            ShelfDetailsUiState.ProductsState.EMPTY -> {}
        }
    }
}
