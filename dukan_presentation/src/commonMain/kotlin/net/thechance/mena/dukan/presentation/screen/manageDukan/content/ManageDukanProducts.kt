package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.empty_shelf
import mena.dukan_presentation.generated.resources.shelf_empty_body
import mena.dukan_presentation.generated.resources.shelf_empty_title
import net.thechance.mena.dukan.presentation.component.EmptyStateContent
import net.thechance.mena.dukan.presentation.component.LoadingVerticalList
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanProductsList
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ProductsState

@Composable
fun ManageDukanProducts(
    state: ManageDukanUiState,
    pager: Pager<Int, ProductUiState>,
    onProductClick: (ProductUiState) -> Unit
) {
    AnimatedContent(
        targetState = state.productState,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "ContentAnimation"
    ) { target ->
        when (target) {
            ProductsState.LOADING -> {
                LoadingVerticalList { LoadingProductCard() }
            }

            ProductsState.LOADED -> ManageDukanProductsList(
                products = state.products.items,
                onProductClick = onProductClick,
                pager = pager
            )

            ProductsState.EMPTY -> EmptyStateContent(
                image = Res.drawable.empty_shelf,
                title = Res.string.shelf_empty_title,
                body = Res.string.shelf_empty_body
            )
        }
    }
}