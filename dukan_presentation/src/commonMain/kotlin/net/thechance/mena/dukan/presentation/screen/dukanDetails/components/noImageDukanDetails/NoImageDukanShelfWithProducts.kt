package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.NoImageDukanProductAction
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.ProductsHeader
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanInfo
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanShelfWithProducts(
    shelf: ShelfUiState,
    listener: DukanDetailsInteractionListener,
    dukanColor: Long,
) {

    Column(
        Modifier.padding(horizontal = Theme.spacing._16)
            .padding(top = Theme.spacing._16)
    ) {
        ProductsHeader(
            shelfName = shelf.name,
            onClick = {
                listener.onViewAllProductsShelfClicked(
                    shelf.id,
                    shelf.name
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._8),
            viewAllColor = Color(dukanColor)
        )
        shelf.products.forEachIndexed { index, product ->
            val topPadding = if (index > 0) Theme.spacing._8 else 0.dp
            ProductItem(
                product = product,
                listener = listener,
                dukanColor = Color(dukanColor),
                modifier = Modifier.padding(top = topPadding)
            )
        }
    }
}


@Composable
private fun ProductItem(
    product: ProductUiState,
    listener: DukanDetailsInteractionListener,
    dukanColor: Color,
    modifier: Modifier = Modifier
) {
    var toggleCartToQuantity by rememberSaveable { mutableStateOf(product.inCartQuantity > 0) }
    var productQuantity by rememberSaveable { mutableIntStateOf(product.inCartQuantity) }

    LaunchedEffect(product) {
        toggleCartToQuantity = product.inCartQuantity > 0
        productQuantity = product.inCartQuantity
    }

    ProductCard(
        productName = product.name,
        productImageUrl = product.imageUrl,
        productDescription = product.description,
        productPrice = product.price,
        productCardBackground = Theme.colorScheme.background.surfaceLow,
        modifier = modifier,
        onProductClick = { listener.onProductClicked(product.id) },
        productAction = {
            NoImageDukanProductAction(
                showProductQuantity = toggleCartToQuantity,
                inCartQuantity = productQuantity,
                dukanColor = dukanColor,
                onAddToCartClick = {
                    toggleCartToQuantity = true
                    productQuantity += 1
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

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun ShelfWithProductsNoImageDukanPreview() {
    MenaTheme {
        NoImageDukanShelfWithProducts(
            shelf = ShelfUiState(
                name = "Clothes",
                products = fakeProducts
            ),
            listener = PreviewDukanDetailsInteractionListener,
            dukanColor = fakeDukanInfo.color
        )
    }
}