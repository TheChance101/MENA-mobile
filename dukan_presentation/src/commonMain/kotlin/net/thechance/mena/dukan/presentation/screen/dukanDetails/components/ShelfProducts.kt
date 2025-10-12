package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShelfProducts(
    state: ShelfDetailsUiState,
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
                cartProductIcon = {
                    CartOrQuantityProductComponent(
                        productUiState = state
                    )
                }
            )

            ShelfDetailsUiState.ProductsState.EMPTY -> {}
        }
    }
}


@Composable
private fun ProductCart(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(RoundedCornerShape(size = Theme.radius.full))
            .background(
                color = Theme.colorScheme.primary.primary,
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_add_shopping_basket),
            contentDescription = stringResource(Res.string.add_shopping_basket),
            modifier = modifier
                .size(16.dp)
        )
    }
}

@Composable
private fun CartOrQuantityProductComponent(
    productUiState: ShelfDetailsUiState
) {
    AnimatedContent(
        targetState = productUiState.showProductQuantity,
        transitionSpec = { fadeTransitionSpec() },
        label = "CartToQuantity"
    ) {
        if (it) {
            SetProductQuantity(
                onAddProductClick = {},
                onRemoveProductClick = {}
            )
        } else {
            ProductCart(
                onClick = {
                    productUiState.copy(
                        showProductQuantity = true
                    )
                },
            )
        }
    }
}
