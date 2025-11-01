package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanInfo
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
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
            ProductCard(
                productName = product.name,
                productImageUrl = product.imageUrl,
                productDescription = product.description,
                productPrice = product.price,
                productAction = {
                    NoImageDukanProductAction(
                        inCartQuantity = product.inCartQuantity,
                        showProductQuantity = product.showProductQuantity,
                        dukanColor = Color(dukanColor),
                        onAddToCartClick = { listener.onAddToCartClicked(product) },
                        onPlusClick = { listener.onPlusClicked(product) },
                        onMinusClick = { listener.onMinusClicked(product) }
                    )
                },
                modifier = Modifier.padding(top = topPadding)
            )
        }
    }
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