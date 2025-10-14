package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ProductsHeader
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState

@Composable
fun ShelfWithProductsNoImageDukan(
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
                listener.onViewAllShelfProductsClicked(
                    shelf.id,
                    shelf.name
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._8)
        )
        shelf.products.forEachIndexed { index, product ->
            val topPadding = if (index > 0) Theme.spacing._8 else 0.dp
            ProductCard(
                productName = product.name,
                productImageUrl = product.imageUrl,
                productDescription = product.description,
                productPrice = product.price,
                productAction = {
                    ProductActionIconNoImageDukan(
                        dukanColor = dukanColor,
                        onClick = { }
                    )
                },
                modifier = Modifier.padding(top = topPadding)
            )
        }
    }
}