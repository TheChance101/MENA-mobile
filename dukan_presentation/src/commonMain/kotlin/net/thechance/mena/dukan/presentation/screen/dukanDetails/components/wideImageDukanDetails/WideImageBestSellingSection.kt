package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.best_selling
import mena.dukan_presentation.generated.resources.wide_image_shoppingcart
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.SmallAndWideImageDukanProductAction
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BestSellingSection(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelves: LazyPagingItems<ShelfUiState>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = stringResource(Res.string.best_selling),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._16)
        )
        AnimatedContent(
            targetState = shelves.loadState.refresh,
        ) {
            if (it is LoadState.NotLoading) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),

                    ) {
                    items(state.bestSellingProducts.size) {
                        val product = state.bestSellingProducts[it]
                        BestSellingItem(
                            imageUrl = product.imageUrl,
                            title = product.name,
                            basePrice = product.basePrice,
                            finalPrice = product.finalPrice,
                            isOutOfStock = product.isOutOfStock,
                            onClick = { listener.onProductClicked(product.id) },
                            productAction = {
                                val quantity = state.productQuantity[product.id] ?: 0
                                SmallAndWideImageDukanProductAction(
                                    showProductQuantity = quantity > 0,
                                    inCartQuantity = quantity,
                                    dukanColor = Color(state.dukanInfo.color),
                                    cartIcon = painterResource(Res.drawable.wide_image_shoppingcart),
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
                            },
                        )
                    }
                }
            }
        }
    }
}