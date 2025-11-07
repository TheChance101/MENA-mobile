package net.thechance.mena.dukan.presentation.screen.shelfDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.loading.LoadingVerticalList
import net.thechance.mena.dukan.presentation.component.product.NoImageDukanProductAction
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.product.SmallAndWideImageDukanProductAction
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState.Style
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShelfProducts(
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
) {
    val dukanStyle = state.dukanStyle
    val productCardBackground = if (dukanStyle == Style.NO_IMAGE) null
    else Theme.colorScheme.background.surfaceLow


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
    productCardBackground: Color?,
    products: LazyPagingItems<ShelfDetailsUiState.ProductUiState>,
    listener: ShelfDetailsInteractionListener,
    state: ShelfDetailsUiState
) {
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
                            state = state,
                            listener = listener,
                            product = product
                        )
                    },
                    onProductClick = { listener.onProductClicked(product.id) },
                )
            }
        }
    }
}

@Composable
private fun CartProductAction(
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    product: ShelfDetailsUiState.ProductUiState
) {

    AnimatedContent(
        targetState = state.dukanStyle,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "CartProductIconAnimation"
    ) { currentStyle ->
        ProductIconAction(
            style = currentStyle,
            state = state,
            listener = listener,
            product = product
        )
    }
}

@Composable
private fun ProductIconAction(
    style: Style,
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    product: ShelfDetailsUiState.ProductUiState
) {
    var toggleCartToQuantity by rememberSaveable { mutableStateOf(product.inCartQuantity > 0) }
    var productQuantity by rememberSaveable { mutableIntStateOf(product.inCartQuantity) }

    when (style) {
        Style.SMALL_IMAGE -> {
            SmallAndWideImageDukanProductAction(
                showProductQuantity = toggleCartToQuantity,
                inCartQuantity = productQuantity,
                dukanColor = Color(state.dukancolor),
                cartIcon = painterResource(Res.drawable.ic_add_shopping_basket),
                onAddToCartClick = {
                    productQuantity += 1
                    toggleCartToQuantity = true
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

        else -> {
            NoImageDukanProductAction(
                showProductQuantity = toggleCartToQuantity,
                inCartQuantity = productQuantity,
                dukanColor = Color(state.dukancolor),
                onAddToCartClick = {
                    productQuantity += 1
                    toggleCartToQuantity = true
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
    }
}