package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.out_of_stock
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.wide_image_shoppingcart
import mena.dukan_presentation.generated.resources.wide_product_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.AppTheme
import net.thechance.mena.dukan.presentation.component.product.PriceWithIcon
import net.thechance.mena.dukan.presentation.component.product.SmallAndWideImageDukanProductAction
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

fun LazyGridScope.wideImageProductsGrid(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    productsShelf: LazyPagingItems<DukanDetailsUiState.ProductUiState>,
) {

    items(
        count = productsShelf.itemCount,
        key = { index -> productsShelf[index]?.id ?: index }
    ) { index ->
        productsShelf[index]?.let { product ->
            val quantity = state.productQuantity[product.id] ?: 0
            ProductCard(
                imageUrl = product.imageUrl,
                title = product.name,
                price = "${product.price}",
                isOutOfStock = product.isOutOfStock,
                onClick = { listener.onProductClicked(product.id) },
                productAction = {
                    if (product.isOutOfStock.not()) {
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
                    }
                }
            )
        }
    }
}

@Composable
private fun ProductCard(
    imageUrl: String,
    title: String,
    price: String,
    isOutOfStock: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    productAction: @Composable () -> Unit,
) {
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .size(width = 160.dp, height = 240.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(SquircleShape(Theme.radius.sm))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(onClick = onClick, indication = null, interactionSource = null)
                .padding(Theme.spacing._4)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(Res.string.wide_product_image),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        isError = state is AsyncImagePainter.State.Error
                        isLoading = state is AsyncImagePainter.State.Loading
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp)
                        .clip(SquircleShape(Theme.radius.sm))
                )

                if (isError || isLoading) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_no_image_loaded),
                        contentDescription = null,
                        tint = Theme.colorScheme.primary.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center),
                    )
                }

                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 12.dp)
                ) {
                    productAction()
                }
            }

            Text(
                text = title,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1,
                modifier = Modifier.padding(top = Theme.spacing._16, bottom = Theme.spacing._4)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.discount_icon),
                    contentDescription = stringResource(Res.string.discount_icon),
                    tint = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(end = Theme.spacing._4)
                )
                PriceWithIcon(
                    price = price,
                    iconRes = Res.drawable.silver_tc,
                    contentDescription = stringResource(Res.string.koin_icon),
                )
            }
        }
        if (isOutOfStock)
            OutOfStockLabel()
    }
}

@Composable
private fun BoxScope.OutOfStockLabel() {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = Theme.spacing._8)
                .offset(x = -Theme.spacing._8)
                .clip(SquircleShape(topStart = 6.dp, bottomEnd = 6.dp, topEnd = 6.dp))
                .background(Theme.colorScheme.brand.brand)
                .padding(vertical = Theme.spacing._4, horizontal = Theme.spacing._8)
        ) {
            Text(
                text = stringResource(Res.string.out_of_stock),
                style = Theme.typography.label.extraSmall,
                color = Theme.colorScheme.primary.onPrimary,
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.TopStart)
                .size(8.dp)
                .offset(x = (-8).dp, y = ((-1).dp))
        ) {
            val badgeColor = Theme.colorScheme.brand.brand
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    if (isRtl) {
                        moveTo(0f, 0f)
                        lineTo(0f, size.height)
                        lineTo(size.width, 0f)
                    } else {
                        moveTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, 0f)
                    }
                    close()
                }
                drawPath(
                    path = path,
                    color = badgeColor
                )
            }
    }
}

@Preview(showBackground = true, name = "Product Card")
@Composable
private fun ProductCardPreview() {
    MenaTheme(
        appTheme = AppTheme.LIGHT.name
    ) {
        ProductCard(
            imageUrl = "https://via.placeholder.com/160x240",
            title = "Fresh Red Apples",
            price = "$1.99",
            modifier = Modifier.padding(8.dp),
            productAction = {
                SmallAndWideImageDukanProductAction(
                    showProductQuantity = false,
                    inCartQuantity = 1,
                    dukanColor = Theme.colorScheme.primary.primary,
                    onAddToCartClick = {},
                    onPlusClick = {},
                    onMinusClick = {},
                    cartIcon = painterResource(Res.drawable.wide_image_shoppingcart)
                )
            },
            onClick = {},
            isOutOfStock = false
        )
    }
}
