package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import mena.dukan_presentation.generated.resources.wide_image_shoppingcart
import mena.dukan_presentation.generated.resources.wide_product_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.PriceWithIcon
import net.thechance.mena.dukan.presentation.component.product.SmallAndWideImageDukanProductAction
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

fun LazyGridScope.wideImageProductsGrid(
    listener: DukanDetailsInteractionListener,
    cartColor: Color,
    productsShelf: LazyPagingItems<DukanDetailsUiState.ProductUiState>,
) {

    items(
        count = productsShelf.itemCount,
        key = { index -> productsShelf[index]?.id ?: index }
    ) { index ->
        productsShelf[index]?.let { product ->
            var toggleCartToQuantity by rememberSaveable { mutableStateOf(product.inCartQuantity > 0) }
            var productQuantity by rememberSaveable { mutableIntStateOf(product.inCartQuantity) }

            ProductCard(
                imageUrl = product.imageUrl,
                title = product.name,
                price = "${product.price}",
                onClick = { listener.onProductClicked(product.id) },
                productAction = {
                    SmallAndWideImageDukanProductAction(
                        showProductQuantity = toggleCartToQuantity,
                        inCartQuantity = productQuantity,
                        dukanColor = cartColor,
                        cartIcon = painterResource(Res.drawable.wide_image_shoppingcart),
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
            )
        }
    }
}

@Composable
private fun ProductCard(
    imageUrl: String,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    productAction: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .size(width = 160.dp, height = 240.dp)
            .clip(RoundedCornerShape(Theme.radius.sm))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp)
                    .clip(RoundedCornerShape(Theme.radius.sm))
            )
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
                modifier = Modifier.padding(end = Theme.spacing._4)
            )
            PriceWithIcon(
                price = price,
                iconRes = Res.drawable.silver_tc,
                contentDescription = stringResource(Res.string.koin_icon),
            )
        }
    }
}

@Preview(showBackground = true, name = "Product Card")
@Composable
private fun ProductCardPreview() {
    MenaTheme {
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
            onClick = {}
        )
    }
}
