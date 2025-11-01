package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.foundation.background
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
    cartColor : Color,
    productsShelf: LazyPagingItems<DukanDetailsUiState.ProductUiState>,
) {
    items(
        count = productsShelf.itemCount,
    ) { index ->
        productsShelf[index]?.let { product ->
            ProductCard(
                imageUrl = product.imageUrl,
                title = product.name,
                price = "${product.price}",
                productAction = {
                    SmallAndWideImageDukanProductAction(
                        showProductQuantity = product.showProductQuantity,
                        inCartQuantity = product.inCartQuantity,
                        cartColor = cartColor,
                        onAddToCartClick = { listener.onAddToCartClicked(product) },
                        onPlusClick = { listener.onPlusClicked(product) },
                        onMinusClick = { listener.onMinusClicked(product) },
                        cartIcon = painterResource(Res.drawable.wide_image_shoppingcart)
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
    productAction: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .size(width = 160.dp, height = 240.dp)
            .clip(RoundedCornerShape(Theme.radius.sm))
            .background(Theme.colorScheme.background.surfaceLow)
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
                    cartColor = Theme.colorScheme.primary.primary,
                    onAddToCartClick = {},
                    onPlusClick = {},
                    onMinusClick = {},
                    cartIcon = painterResource(Res.drawable.wide_image_shoppingcart)
                )
            }
        )
    }
}
