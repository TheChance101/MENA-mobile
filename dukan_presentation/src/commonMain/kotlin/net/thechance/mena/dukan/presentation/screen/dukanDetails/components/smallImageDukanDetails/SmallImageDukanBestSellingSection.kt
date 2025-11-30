package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.best_selling
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_no_image_loaded
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.SmallAndWideImageDukanProductAction
import net.thechance.mena.dukan.presentation.component.shared.ProductPrice
import net.thechance.mena.dukan.presentation.util.modifiers.fillWidthOfParent
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
fun SmallImageDukanBestSellingSection(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = stringResource(Res.string.best_selling),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(top = Theme.spacing._16, start = 16.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillWidthOfParent(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(state.bestSellingProducts.size) {
                val product = state.bestSellingProducts[it]
                BestSellingItem(
                    modifier = Modifier.width(140.dp)
                        .clickable(
                            onClick = { listener.onProductClicked(product.id) },
                            indication = null,
                            interactionSource = null
                        ),
                    product = product,
                    listener = listener,
                    dukanColor = Color(state.dukanInfo.color),
                    quantity = state.productQuantity[product.id] ?: 0
                )
            }
        }
    }
}

@Composable
private fun BestSellingItem(
    product: ProductUiState,
    listener: DukanDetailsInteractionListener,
    quantity: Int,
    dukanColor: Color,
    modifier: Modifier = Modifier
) {
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(164.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .clip(SquircleShape(Theme.radius.md))
                    .size(136.dp, 160.dp),
                onState = {
                    isError = it is AsyncImagePainter.State.Error
                    isLoading = it is AsyncImagePainter.State.Loading

                },
                contentScale = ContentScale.Crop
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
            SmallAndWideImageDukanProductAction(
                modifier = Modifier.align(Alignment.BottomEnd),
                showProductQuantity = quantity > 0,
                inCartQuantity = quantity,
                dukanColor = dukanColor,
                cartIcon = painterResource(Res.drawable.ic_add_shopping_basket),
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
        Text(
            text = product.name,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1
        )
        ProductPrice(product.basePrice, product.finalPrice)
    }
}