package net.thechance.mena.dukan.presentation.screen.dukanCart.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SwipeableItem
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.product.ProductQuantityButton
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.screen.dukanCart.components.DukanCartBottomBar
import net.thechance.mena.dukan.presentation.screen.dukanCart.components.DukanCartDeleteActionButton
import net.thechance.mena.dukan.presentation.screen.dukanCart.components.DukanCartInfo
import net.thechance.mena.dukan.presentation.screen.dukanCart.components.DukanCartInfoSkeleton
import net.thechance.mena.dukan.presentation.screen.dukanCart.components.DukanCartTopBar
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanCartInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.dukanCartUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanCartContent(
    state: DukanCartUiState,
    listener: DukanCartInteractionListener
) {
    val products = state.products.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    OnSystemBackPressed(listener::onBackClicked)


    Scaffold(
        topBar = { DukanCartTopBar(listener::onBackClicked) },
        bottomBar = {
            DukanCartBottomBar(
                totalPrice = state.totalPrice,
                onCheckoutClick = listener::onCheckoutClicked
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = Theme.spacing._8),
            modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16),
            state = lazyListState,
        ) {
            item(key = "dukan_info") {
                AnimatedContent(state.dukanInfoState) {
                    when (it) {
                        DukanCartUiState.DukanInfoState.LOADING -> DukanCartInfoSkeleton()
                        DukanCartUiState.DukanInfoState.LOADED -> {
                            DukanCartInfo(
                                dukanName = state.dukanInfo.name,
                                dukanImageUrl = state.dukanInfo.imageUrl,
                                onClick = listener::onDukanClicked
                            )
                        }
                    }
                }
            }

            if (products.loadState.refresh is LoadState.Loading) {
                items(count = 8) {
                    LoadingProductCard()
                }
            } else {
                items(
                    count = products.itemCount,
                    key = { products[it]?.id.orEmpty() }) { index ->
                    val product = products[index] ?: return@items
                    val productQuantityInCart =
                        state.productQuantity[product.id] ?: product.quantity

                    SwipeableItem(
                        actionButton = {
                            DukanCartDeleteActionButton(
                                onRemoveItemClicked = {
                                    listener.onRemoveItemClicked(product.id)
                                }
                            )
                        }
                    ) {
                        ProductCard(
                            name = product.name,
                            description = product.description,
                            imageUrl = product.imageUrl,
                            basePrice = product.basePrice,
                            finalPrice = product.finalPrice,
                            backgroundColor = Theme.colorScheme.background.surfaceLow,
                            modifier = Modifier.fillMaxWidth(),
                            productAction = {
                                ProductQuantityButton(
                                    onPlusClick = {
                                        listener.onIncreaseItemQuantityClicked(
                                            product.id,
                                            productQuantityInCart + 1
                                        )
                                    },
                                    onMinusClick = {
                                        listener.onDecreaseItemQuantityClicked(
                                            product.id,
                                            productQuantityInCart - 1
                                        )
                                    },
                                    inCartQuantity = productQuantityInCart
                                )
                            },
                            isOutOfStock = product.isOutOfStock,
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun DukanCartContentPreview() {
    MenaTheme {
        DukanCartContent(
            state = dukanCartUiState,
            listener = PreviewDukanCartInteractionListener
        )
    }
}

@Preview
@Composable
private fun DukanCartContentLoadingPreview() {
    MenaTheme {
        DukanCartContent(
            state = DukanCartUiState(),
            listener = PreviewDukanCartInteractionListener
        )
    }
}