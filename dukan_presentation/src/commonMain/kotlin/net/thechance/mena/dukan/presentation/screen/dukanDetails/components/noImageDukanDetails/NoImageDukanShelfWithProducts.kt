package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.NoImageDukanProductAction
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.ProductsHeader
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProductsLimited
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanShelfWithProducts(
    shelf: ShelfUiState,
    products: List<ProductUiState>,
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
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
            viewAllColor = Color(state.dukanInfo.color)
        )
        val spacing = Theme.spacing._8
        val minCardWidth = 320.dp

        LazyVerticalGrid(
            modifier = Modifier.heightIn(min = 0.dp, max = 10_000.dp),
            columns = GridCells.Adaptive(minSize = minCardWidth),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing)
        ) {
            items(products) { product ->
                ProductItem(
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
private fun ProductItem(
    product: ProductUiState,
    listener: DukanDetailsInteractionListener,
    quantity: Int,
    dukanColor: Color,
    modifier: Modifier = Modifier
) {
    ProductCard(
        name = product.name,
        imageUrl = product.imageUrl,
        description = product.description,
        basePrice = product.basePrice,
        finalPrice = product.finalPrice,
        isOutOfStock = product.isOutOfStock,
        isDukanStyleNoImage = true,
        modifier = modifier,
        onProductClick = { listener.onProductClicked(product.id) },
        productAction = {
            if (product.isOutOfStock.not()) {
                NoImageDukanProductAction(
                    showProductQuantity = quantity > 0,
                    inCartQuantity = quantity,
                    dukanColor = dukanColor,
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

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun ShelfWithProductsNoImageDukanPreview() {
    MenaTheme {
        NoImageDukanShelfWithProducts(
            shelf = ShelfUiState(name = "Clothes"),
            listener = PreviewDukanDetailsInteractionListener,
            state = fakeDukanDetails,
            products = fakeProductsLimited["1"] ?: emptyList()
        )
    }
}