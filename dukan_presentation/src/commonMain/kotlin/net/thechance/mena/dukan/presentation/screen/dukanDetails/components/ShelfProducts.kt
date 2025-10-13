package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState

@Composable
fun ShelfProducts(
    state: ShelfDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pager: Pager<Int, ShelfDetailsUiState.ProductUiState>,
) {
    AnimatedContent(
        targetState = state.productsState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ProductContentAnimation"
    ) { target ->
        when (target) {
            ShelfDetailsUiState.ProductsState.LOADING -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = Theme.spacing._8),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(
                        horizontal = Theme.spacing._16,
                        vertical = Theme.spacing._8
                    ),
                ) {
                    items(8) {
                        LoadingProductCard()
                    }
                }
            }

            ShelfDetailsUiState.ProductsState.LOADED -> ProductsList(
                products = state.productsShelf,
                pager = pager,
                cartProductIcon = {productId->
                    CartOrQuantityProductComponent(
                        showProductQuantity = state.productsShelf.items.first { it.id == productId }.showProductQuantity,
                        onCartClick = { listener.onCartClick(productId) }
                    )
                }
            )

            ShelfDetailsUiState.ProductsState.EMPTY -> {}
        }
    }
}
